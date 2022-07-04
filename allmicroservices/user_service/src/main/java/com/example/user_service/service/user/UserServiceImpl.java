package com.example.user_service.service.user;


import com.example.user_service.config.GoogleOauthCheck;
import com.example.user_service.config.PdfMailSender;
import com.example.user_service.exception.DataAccessExceptionMessage;
import com.example.user_service.exception.GoogleSsoException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.medicine.MedicineHistory;
import com.example.user_service.model.user.UserDetails;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.pojos.authentication.GoogleOauthData;
import com.example.user_service.pojos.dto.user.UserEntityDTO;
import com.example.user_service.pojos.dto.user.UserEntityDetailsDto;
import com.example.user_service.pojos.dto.user.UserMailDto;
import com.example.user_service.pojos.response.medicine.PdfLinkResponse;
import com.example.user_service.pojos.response.user.UserResponse;
import com.example.user_service.repository.user.UserDetailsRepository;
import com.example.user_service.repository.medicine.UserMedicineRepository;
import com.example.user_service.repository.user.UserRepository;
import com.example.user_service.service.medicine.UserMedicineService;
import com.example.user_service.util.Datehelper;

import com.example.user_service.util.JwtUtil;
import com.example.user_service.util.Messages;
import org.hibernate.exception.JDBCConnectionException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ModelMapper mapper;
    private final PdfMailSender pdfMailSender;
    private final UserMedicineService userMedicineService;
    private final UserMedicineRepository userMedicineRepository;

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
  //  Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    GoogleOauthCheck googleOauthCheck;

    UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserMedicineRepository userMedicineRepository, UserDetailsRepository userDetailsRepository, ModelMapper mapper, PdfMailSender pdfMailSender, GoogleOauthCheck googleOauthCheck, UserMedicineService userMedicineService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userMedicineRepository = userMedicineRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.mapper = mapper;
        this.pdfMailSender = pdfMailSender;
        this.googleOauthCheck = googleOauthCheck;
        this.userMedicineService = userMedicineService;
    }

    @Override
    public UserResponse saveUser(UserEntityDTO userEntityDTO, String fcmToken, String picPath) throws UserExceptionMessage, GoogleSsoException {
        try {
            googleOauthCheck.checkForGoogleaccount("", userEntityDTO.getEmail());
            UserEntity user = userRepository.findByMail(userEntityDTO.getEmail());
            if (user != null) {
                return new UserResponse(Messages.FAILED, Messages.USER_ALREADY_PRESENT, new ArrayList<>(Arrays.asList(user)), "", "");
            }

            UserEntity userEntity = mapToEntity(userEntityDTO);
            userEntity.setLastLogin(Datehelper.getcurrentdatatime());
            userEntity.setCreatedAt(Datehelper.getcurrentdatatime());
            UserDetails userDetails = new UserDetails();
            userDetails.setFcmToken(fcmToken);
            userDetails.setPicPath(picPath);
            userEntity.setUserDetails(userDetails);
            userDetailsRepository.save(userDetails);
            UserEntity ue = userRepository.save(userEntity);
            if (ue.getUserName() == null) {
                throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN);

            }
            String jwtToken = jwtUtil.generateToken(ue.getUserName());
            String refreshToken = passwordEncoder.encode(ue.getUserId());

            return new UserResponse(Messages.SUCCESS, Messages.SAVED_USER_SUCCESSFULLY, new ArrayList<>(Arrays.asList(ue)), jwtToken, refreshToken);
        } catch (DataAccessException | JDBCConnectionException accessException) {
            com.example.user_service.config.Log.Logger.errorLog("UserService",accessException.getMessage());
            throw new DataAccessExceptionMessage(Messages.ERROR_TRY_AGAIN + accessException.getMessage());
        }
    }

    @Override
    @Async
    public CompletableFuture<List<UserEntity>> getUsers() throws UserExceptionMessage {

        try {
            Pageable pageable = PageRequest.of(0, 1);
            List<UserEntity> list = userRepository.findAll(pageable).getContent();

            return CompletableFuture.completedFuture(list);
        }  catch (DataAccessException | JDBCConnectionException accessException) {
            com.example.user_service.config.Log.Logger.errorLog("UserService",accessException.getMessage());
            throw new DataAccessExceptionMessage(Messages.ERROR_TRY_AGAIN + accessException.getMessage());
        }
    }

    @Override
    public UserEntity getUserById(String userId) throws UserExceptionMessage {
        try {
            Optional<UserEntity> optionalUserEntity = Optional.ofNullable(userRepository.getUserById(userId));
            if (optionalUserEntity.isEmpty()) {
                throw new UserExceptionMessage(Messages.DATA_NOT_FOUND);
            }

            return optionalUserEntity.get();
        }  catch (DataAccessException | JDBCConnectionException accessException) {
            com.example.user_service.config.Log.Logger.errorLog("UserService",accessException.getMessage());
            throw new DataAccessExceptionMessage(Messages.ERROR_TRY_AGAIN + accessException.getMessage());
        }
    }




    @Override
    public UserEntity updateUser(String userId, UserEntityDTO userEntityDTO) {
        try {
            UserEntity userDB = userRepository.getUserById(userId);
            UserEntity userEntity = mapToEntity(userEntityDTO);
            if (Objects.nonNull(userEntity.getUserName()) && !"".equalsIgnoreCase(userEntity.getUserName())) {
                userDB.setUserName(userEntity.getUserName());
            }
            if (Objects.nonNull(userEntity.getEmail()) && !"".equalsIgnoreCase(userEntity.getEmail())) {
                userDB.setEmail(userEntity.getEmail());
            }

            return userRepository.save(userDB);
        }  catch (DataAccessException | JDBCConnectionException accessException) {
            com.example.user_service.config.Log.Logger.errorLog("UserService",accessException.getMessage());
            throw new DataAccessExceptionMessage(Messages.ERROR_TRY_AGAIN + accessException.getMessage());
        }

    }

    @Override
    public List<UserEntity> getUserByName(String userName) throws UserExceptionMessage, NullPointerException {

        try {
            List<UserEntity> userEntity = userRepository.findByNameIgnoreCase(userName);
            if (userEntity.isEmpty()) {
                throw new UserExceptionMessage(Messages.DATA_NOT_FOUND);
            }
            return userEntity;
        } catch (DataAccessException  | JDBCConnectionException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR_MSG + dataAccessException.getMessage());
        }

    }

    @Override
    public UserMailDto getUserByEmail(String email) throws UserExceptionMessage {

        try {
            return userRepository.searchByMail(email);
        }  catch (DataAccessException | JDBCConnectionException accessException) {
            com.example.user_service.config.Log.Logger.errorLog("UserService",accessException.getMessage());
            throw new DataAccessExceptionMessage(Messages.ERROR_TRY_AGAIN + accessException.getMessage());
        }

    }


    @Override
    public PdfLinkResponse sendUserMedicines(Integer medId) {
        try {
            Optional<UserMedicines> userMedicines = userMedicineRepository.findById(medId);
            if (userMedicines.isEmpty()) {
                return new PdfLinkResponse(Messages.FAILED,Messages.ERROR_TRY_AGAIN,null);
            }
            UserEntity entity = userMedicines.get().getUserEntity();
            List<MedicineHistory> medicineHistories = userMedicines.get().getMedicineHistories();
            return new PdfLinkResponse(Messages.SUCCESS,Messages.PDF_SUCCESS,pdfMailSender.send(entity, userMedicines.get(), medicineHistories));
        }  catch (DataAccessException | JDBCConnectionException accessException) {
            com.example.user_service.config.Log.Logger.errorLog("UserService",accessException.getMessage());
            throw new DataAccessExceptionMessage(Messages.ERROR_TRY_AGAIN + accessException.getMessage());
        }
    }

    @Override
    public UserResponse login(String mail, String fcmToken) throws UserExceptionMessage {
        try {
            UserEntity user = userRepository.findByMail(mail);
            UserDetails userDetails = user.getUserDetails();
            userDetails.setFcmToken(fcmToken);
            userDetailsRepository.save(userDetails);
            user = userRepository.findByMail(mail);
            if (user != null) {
                String jwtToken = jwtUtil.generateToken(user.getUserName());
                String refreshToken = passwordEncoder.encode(user.getUserId());
                return new UserResponse(Messages.SUCCESS, Messages.ACCOUNT_CREATED, new ArrayList<>(Arrays.asList(user)), jwtToken, refreshToken);
            }
            throw new UserExceptionMessage(Messages.DATA_NOT_FOUND);

        }  catch (DataAccessException | JDBCConnectionException accessException) {
            com.example.user_service.config.Log.Logger.errorLog("UserService",accessException.getMessage());
            throw new DataAccessExceptionMessage(Messages.ERROR_TRY_AGAIN + accessException.getMessage());
        }


    }

    private UserEntity mapToEntity(UserEntityDTO userEntityDTO) {
        return mapper.map(userEntityDTO, UserEntity.class);
    }
}
