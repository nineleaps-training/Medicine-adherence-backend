package com.example.user_service.exception;

public class DataAccessExceptionMessage extends RuntimeException {

    public DataAccessExceptionMessage(String msg) {
        super(msg);
    }
}
