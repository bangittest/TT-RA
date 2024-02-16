package com.example.warehousemanagement_team1.exception;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class WarehouseException extends Exception{
    public WarehouseException(String errorCode, MessageSource messageSource) {
        super(messageSource.getMessage(errorCode, null, Locale.getDefault()));
    }

    public WarehouseException(String message) {
        super(message);
    }
}
