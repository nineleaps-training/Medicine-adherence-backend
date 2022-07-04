package com.example.user_service.service.user;

import com.example.user_service.exception.GoogleSsoException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.user.UserEntityDTO;
import com.example.user_service.pojos.dto.user.UserEntityDetailsDto;
import com.example.user_service.pojos.dto.user.UserMailDto;
import com.example.user_service.pojos.response.medicine.PdfLinkResponse;
import com.example.user_service.pojos.response.user.UserResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public interface UserService {

     UserResponse saveUser(UserEntityDTO userEntityDTO, String fcmToken, String picPath) throws UserExceptionMessage, GoogleSsoException;

     CompletableFuture<List<UserEntity>> getUsers() throws UserExceptionMessage;

     UserEntity getUserById(String userId) throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException;

     UserEntity updateUser(String userId, UserEntityDTO userEntityDTO)throws UserExceptionMessage;

     List<UserEntity> getUserByName(String userName)throws UserExceptionMessage;

     UserMailDto getUserByEmail(String email) throws UserExceptionMessage;

     PdfLinkResponse sendUserMedicines(Integer userId) throws IOException;

     UserResponse login(String mail , String fcmToken) throws UserExceptionMessage;



}
