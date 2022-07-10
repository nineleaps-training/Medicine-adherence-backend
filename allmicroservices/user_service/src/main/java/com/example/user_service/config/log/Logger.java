package com.example.user_service.config.log;

import org.slf4j.LoggerFactory;

public class Logger {

    private Logger(){}
   static org.slf4j.Logger  mainLogger = LoggerFactory.getLogger(
            Logger.class
    );

    public  static void errorLog(String module , String errorMessage){
        mainLogger.error(module , errorMessage);
    }
    public  static void infoLog(String module , String errorMessage){
        mainLogger.error(module , errorMessage);
    }
    public  static void ebugLog(String module , String errorMessage){
        mainLogger.error(module , errorMessage);
    }

}
