package com.example.user_service.config.Log;

import org.slf4j.LoggerFactory;

public class Logger {

   static org.slf4j.Logger  logger = LoggerFactory.getLogger(
            Logger.class
    );

    public  static void errorLog(String module , String errorMessage){
        logger.error(module , errorMessage);
    }
    public  static void infoLog(){

    }
    public  static void Log(){

    }

}
