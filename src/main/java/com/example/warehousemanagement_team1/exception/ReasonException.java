package com.example.warehousemanagement_team1.exception;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class ReasonException extends Exception {
    public ReasonException(String s, MessageSource messageSource) {
        super(messageSource.getMessage(s, null, Locale.getDefault()));
    }

    public ReasonException(String message) {
        super(message);
    }
}
