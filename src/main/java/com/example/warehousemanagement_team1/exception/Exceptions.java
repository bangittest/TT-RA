package com.example.warehousemanagement_team1.exception;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class Exceptions extends Exception{
    public Exceptions(String errorCode, MessageSource messageSource) {
        super(messageSource.getMessage(errorCode, null, Locale.getDefault()));
    }
    public Exceptions(String message) {
        super(message);
    }
}
