package com.example.warehousemanagement_team1.exception;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class ResponseException extends Exception{
    public ResponseException(String s, MessageSource messageSource) {
        super(messageSource.getMessage(s, null, Locale.getDefault()));
    }

    public ResponseException(String message) {
        super(message);
    }
}
