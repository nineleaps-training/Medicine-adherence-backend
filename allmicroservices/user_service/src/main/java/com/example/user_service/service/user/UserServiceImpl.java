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
import com.example.user_service.pojos.dto.user.UserEntityDTO;
import com.example.user_service.pojos.dto.user.UserMailDto;
import com.example.user_service.pojos.response.medicine.PdfLinkResponse;
import com.example.user_service.pojos.response.user.UserResponse;
import com.example.user_service.repository.user.UserDetailsRepository;
import com.example.user_service.repository.medicine.UserMedicineRepository;
import com.example.user_service.repository.user.UserRepository;
import com.example.user_service.util.Datehelper;

import com.example.user_service.util.JwtUtil;
import com.example.user_service.util.Messages;
import org.hibernate.HibernateException;
import org.hibernate.exception.JDBCConnectionException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    @Autowired
    private ModelMapper mapper;
    private final PdfMailSender pdfMailSender;
    private final UserMedicineRepository userMedicineRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final GoogleOauthCheck googleOauthCheck;

    public UserServiceImpl(UserRepository userRepository, UserMedicineRepository userMedicineRepository, UserDetailsRepository userDetailsRepository, PdfMailSender pdfMailSender, GoogleOauthCheck googleOauthCheck, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.userMedicineRepository = userMedicineRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.pdfMailSender = pdfMailSender;
        this.googleOauthCheck = googleOauthCheck;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
    }

    @Override
    public UserResponse saveUser(UserEntityDTO userEntityDTO, String fcmToken, String picPath) throws UserExceptionMessage, GoogleSsoException {
        try {
           // googleOauthCheck.checkForGoogleaccount(userEntityDTO.getEmail());
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
            Optional<UserEntity> ue = Optional.of(userRepository.save(userEntity));
            if (ue.get().getUserName() == null) {
                throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN);

            }
            String jwtToken = jwtUtil.generateToken(ue.get().getUserName());
            String refreshToken = passwordEncoder.encode(ue.get().getUserId());

            return new UserResponse(Messages.SUCCESS, Messages.SAVED_USER_SUCCESSFULLY, new ArrayList<>(Arrays.asList(ue.get())), jwtToken, refreshToken);
        } catch (DataAccessException | JDBCConnectionException accessException) {
            com.example.user_service.config.log.Logger.errorLog("UserService", accessException.getMessage());
            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN + accessException.getMessage());
        }
    }

    @Override
    @Async
    public CompletableFuture<List<UserEntity>> getUsers() throws UserExceptionMessage {

        try {
            Pageable pageable = PageRequest.of(0, 1);
            List<UserEntity> list = userRepository.findAllUsers(pageable);

            return CompletableFuture.completedFuture(list);
        } catch (DataAccessException | JDBCConnectionException accessException) {
            com.example.user_service.config.log.Logger.errorLog("UserService", accessException.getMessage());
            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN);
        }
    }

    @Override
    public UserEntity getUserById(String userId) throws UserExceptionMessage {
        try {
            UserEntity user = userRepository.getUserById(userId);
            Optional<UserEntity> optionalUserEntity = Optional.ofNullable(user);

            if (optionalUserEntity.isEmpty()) {
                throw new UserExceptionMessage(Messages.DATA_NOT_FOUND);
            }

            return optionalUserEntity.get();
        } catch (DataAccessException | JDBCConnectionException accessException) {
            com.example.user_service.config.log.Logger.errorLog("UserService", accessException.getMessage());
            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN);
        }
    }


    @Override
    public UserMailDto getUserByEmail(String email) throws UserExceptionMessage {

        try {
            return userRepository.searchByMail(email);
        } catch (DataAccessException | HibernateException accessException) {
            com.example.user_service.config.log.Logger.errorLog("UserService", accessException.getMessage());
            throw new DataAccessExceptionMessage(Messages.ERROR_TRY_AGAIN + accessException.getMessage());
        }

    }


    @Override
    public PdfLinkResponse sendUserMedicines(Integer medId) throws UserExceptionMessage {

        Optional<UserMedicines> userMedicines = userMedicineRepository.findById(medId);
        if (userMedicines.isEmpty()) {
            return new PdfLinkResponse(Messages.FAILED, Messages.ERROR_TRY_AGAIN, null);
        }
        UserEntity entity = userMedicines.get().getUserEntity();
        List<MedicineHistory> medicineHistories = userMedicines.get().getMedicineHistories();
        return new PdfLinkResponse(Messages.SUCCESS, Messages.PDF_SUCCESS, pdfMailSender.send(entity, userMedicines.get(), medicineHistories));

    }

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
            return new UserResponse(Messages.SUCCESS, Messages.ACCOUNT_CREATED, new ArrayList<>(Arrays.asList(user)), jwtToken, refreshToken);


        } catch (DataAccessException | HibernateException accessException) {
            com.example.user_service.config.log.Logger.errorLog("UserService", accessException.getMessage());
            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN + accessException.getMessage());
        }


    }

    private UserEntity mapToEntity(UserEntityDTO userEntityDTO) {
        return mapper.map(userEntityDTO, UserEntity.class);
    }
}
