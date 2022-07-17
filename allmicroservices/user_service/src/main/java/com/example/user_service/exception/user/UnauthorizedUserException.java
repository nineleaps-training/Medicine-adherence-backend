package com.example.user_service.exception.user;

public class UnauthorizedUserException extends Exception{

    public UnauthorizedUserException(String messag){
        super(messag);
    }

}
