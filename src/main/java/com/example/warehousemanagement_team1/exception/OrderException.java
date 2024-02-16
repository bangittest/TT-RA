package com.example.warehousemanagement_team1.exception;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class OrderException extends Exception{
    public OrderException(String errorCode, MessageSource messageSource) {
        super(messageSource.getMessage(errorCode, null, Locale.getDefault()));
    }
    public OrderException(String message) {
        super(message);
    }
}
