package com.example.user_service.service.user;

import java.io.FileNotFoundException;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import com.example.user_service.pojos.response.auth.RefreshTokenResponse;
import com.example.user_service.service.token.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.JDBCConnectionException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.user_service.config.GoogleOauthCheck;
import com.example.user_service.config.PdfMailSender;
import com.example.user_service.exception.user.GoogleSsoException;
import com.example.user_service.exception.user.UserExceptionMessage;
import com.example.user_service.model.medicine.MedicineHistory;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.model.user.UserDetails;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.user.UserEntityDTO;
import com.example.user_service.pojos.dto.user.UserMailDto;
import com.example.user_service.pojos.response.medicine.PdfLinkResponse;
import com.example.user_service.pojos.response.user.GetUsersresponse;
import com.example.user_service.pojos.response.user.UserResponse;
import com.example.user_service.repository.medicine.UserMedicineRepository;
import com.example.user_service.repository.user.UserDetailsRepository;
import com.example.user_service.repository.user.UserRepository;
import com.example.user_service.util.Datehelper;
import com.example.user_service.util.JwtUtil;
import com.example.user_service.util.Messages;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ModelMapper mapper;
    private final PdfMailSender pdfMailSender;
    private final UserMedicineRepository userMedicineRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final GoogleOauthCheck googleOauthCheck;
    private final RefreshToken refreshToken;



    @Override
    public UserResponse login(String mail, String fcmToken) throws UserExceptionMessage {
        try {
            UserEntity user = userRepository.findByMail(mail);

            if (user == null) {
                throw new UserExceptionMessage(Messages.DATA_NOT_FOUND);
            }

            UserDetails userDetails = user.getUserDetails();

            userDetails.setFcmToken(fcmToken);
            userDetailsRepository.save(userDetails);
            user = userDetails.getUser();

            String jwtToken = jwtUtil.generateToken(user.getUserName());
            String refreshToken = passwordEncoder.encode(user.getUserId());

            return new UserResponse(Messages.SUCCESS,
                    Messages.ACCOUNT_CREATED,
                    new ArrayList<>(Arrays.asList(user)),
                    jwtToken,
                    refreshToken);
        } catch (DataAccessException | JDBCConnectionException accessException) {
            com.example.user_service.config.log.Logger.errorLog(Messages.USER_SERVICE, accessException.getMessage());
            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN + accessException.getMessage());
        }
    }

    private UserEntity mapToEntity(UserEntityDTO userEntityDTO) {
        return mapper.map(userEntityDTO, UserEntity.class);
    }

    @Override
    public UserResponse saveUser(UserEntityDTO userEntityDTO, String fcmToken, String picPath)
            throws UserExceptionMessage, GoogleSsoException {
        logger.info("Save user :{}", userEntityDTO);

        try {
          //  googleOauthCheck.checkForGoogleaccount(userEntityDTO.getEmail());
            checkforUser(userEntityDTO);
            UserEntity userEntity = mapToEntity(userEntityDTO);
            userEntity.setLastLogin(Datehelper.getcurrentdatatime());
            userEntity.setCreatedAt(Datehelper.getcurrentdatatime());
            UserDetails userDetails = new UserDetails();
            userDetails.setFcmToken(fcmToken);
            userDetails.setPicPath(picPath);
            userDetails.setUser(userEntity);
            userEntity.setUserDetails(userDetails);
            Optional<UserEntity> ue = Optional.of(userRepository.save(userEntity));

            if (ue.get().getUserName() == null) {
                throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN);
            }
            com.example.user_service.model.Auth.RefreshToken refreshToken1 = refreshToken.createRefreshToken(ue.get().getUserName());
            return new UserResponse(Messages.SUCCESS,
                    Messages.SAVED_USER_SUCCESSFULLY,
                    new ArrayList<>(List.of(ue.get())),
                    jwtUtil.generateToken(ue.get().getUserName())
                    ,refreshToken1.getToken()
                    );
        } catch (JDBCConnectionException accessException) {
            com.example.user_service.config.log.Logger.errorLog(Messages.USER_SERVICE, accessException.getMessage());
            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN + accessException.getMessage());
        }
    }

    @PostMapping(value = "/refreshToken")
    public ResponseEntity<RefreshTokenResponse> getRefreshToken(@RequestParam(name = "userId") String userId){
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    private void checkforUser(UserEntityDTO userEntityDTO) throws UserExceptionMessage {
        if (userRepository.findByMail(userEntityDTO.getEmail()) != null) {
            throw new UserExceptionMessage(Messages.USER_ALREADY_PRESENT);
        }
    }

    @Override
    public PdfLinkResponse sendUserMedicines(Integer medId) throws FileNotFoundException, UserExceptionMessage {
        logger.info("Generate Pdf for medicine : {}", medId);

        try {
            Optional<UserMedicines> userMedicines = userMedicineRepository.findById(medId);

            if (userMedicines.isEmpty()) {
                return new PdfLinkResponse(Messages.FAILED, Messages.ERROR_TRY_AGAIN, null);
            }

            UserEntity entity = userMedicines.get().getUserEntity();
            List<MedicineHistory> medicineHistories = userMedicines.get().getMedicineHistories();

            return new PdfLinkResponse(Messages.SUCCESS,
                    Messages.PDF_SUCCESS,
                    pdfMailSender.send(entity, userMedicines.get(), medicineHistories));
        } catch (DataAccessException | JDBCConnectionException exception) {
            com.example.user_service.config.log.Logger.errorLog(Messages.USER_SERVICE, exception.getMessage());

            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN);
        }
    }

    @Override
    @Cacheable(
            value = "mailcache",
            key = "#email"
    )
    public UserMailDto getUserByEmail(String email)  {
        logger.info("Get User by mail : {}", email);

        try {
            UserMailDto userMailDto = userRepository.searchByMail(email);
            return userMailDto;
        } catch (DataAccessException | JDBCConnectionException accessException) {
            com.example.user_service.config.log.Logger.errorLog(Messages.USER_SERVICE, accessException.getMessage());

            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN);
        }
    }

    @Override
    @Cacheable(
            value = "usercache",
            key = "#userId"
    )
    public UserEntity getUserById(String userId) throws UserExceptionMessage {
        logger.info("Get User Details :{}", userId);
        try {
            UserEntity user = userRepository.getUserById(userId);
            Optional<UserEntity> optionalUserEntity = Optional.ofNullable(user);

            if (optionalUserEntity.isEmpty()) {
                throw new UserExceptionMessage(Messages.DATA_NOT_FOUND);
            }

            return optionalUserEntity.get();
        } catch (DataAccessException | JDBCConnectionException accessException) {
            accessException.printStackTrace();
            com.example.user_service.config.log.Logger.errorLog(Messages.USER_SERVICE, accessException.getMessage());
            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN);
        }
    }

    @Override
    public GetUsersresponse getUsers() throws UserExceptionMessage {
        try {
            Pageable pageable = PageRequest.of(0, 5);
            List<UserEntity> list = userRepository.findAllUsers(pageable);

            return new GetUsersresponse(CompletableFuture.completedFuture(list).get());
        } catch (DataAccessException | JDBCConnectionException accessException) {
            com.example.user_service.config.log.Logger.errorLog(Messages.USER_SERVICE, accessException.getMessage());

            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN);
        } catch (ExecutionException e) {
            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN);
        } catch (InterruptedException e) {
            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN);
        }
    }


}
