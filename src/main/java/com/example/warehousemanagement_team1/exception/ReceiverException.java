package com.example.warehousemanagement_team1.exception;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class ReceiverException extends Exception{
    public ReceiverException(String errorCode, MessageSource messageSource) {
        super(messageSource.getMessage(errorCode, null, Locale.getDefault()));
    }
}
