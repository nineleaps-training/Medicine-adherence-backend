package com.example.user_service.exception.user;

/**
 *  Sends exception message for User
 */
public class UserExceptionMessage extends RuntimeException{

     public UserExceptionMessage(String errorMessage){
         super(errorMessage);
     }

}
//