package com.example.warehousemanagement_team1.exception;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class UserException extends Exception {
//    public UserException(String message) {
//        super(message);
//    }

    public UserException(String errorCode, MessageSource messageSource) {
        super(messageSource.getMessage(errorCode, null, Locale.getDefault()));
    }
    public UserException(String message) {
        super(message);
    }
}
