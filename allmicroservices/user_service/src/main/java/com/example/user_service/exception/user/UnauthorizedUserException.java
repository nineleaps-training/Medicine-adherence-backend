package com.example.user_service.exception.user;

public class UnauthorizedUserException extends RuntimeException{

    public UnauthorizedUserException(String messag){
        super(messag);
    }

}
