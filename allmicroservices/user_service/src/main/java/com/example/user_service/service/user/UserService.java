package com.example.user_service.service.user;

import com.example.user_service.exception.user.GoogleSsoException;
import com.example.user_service.exception.user.UserExceptionMessage;
import com.example.user_service.exception.medicine.UserMedicineException;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.user.UserEntityDTO;
import com.example.user_service.pojos.dto.user.UserMailDto;
import com.example.user_service.pojos.response.medicine.PdfLinkResponse;
import com.example.user_service.pojos.response.user.GetUsersresponse;
import com.example.user_service.pojos.response.user.UserResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public interface UserService {

     UserResponse saveUser(UserEntityDTO userEntityDTO, String fcmToken, String picPath) throws UserExceptionMessage, GoogleSsoException;

     GetUsersresponse getUsers() throws UserExceptionMessage;

     UserEntity getUserById(String userId) throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException;

     UserMailDto getUserByEmail(String email) throws UserExceptionMessage;

     PdfLinkResponse sendUserMedicines(Integer userId) throws IOException, UserExceptionMessage;

     UserResponse login(String mail , String fcmToken) throws UserExceptionMessage;



}
