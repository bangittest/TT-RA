package com.example.warehousemanagement_team1.exception;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class SupplierException extends Exception{
    public SupplierException(String errorCode, MessageSource messageSource) {
        super(messageSource.getMessage(errorCode, null, Locale.getDefault()));
    }
}
