package com.example.warehousemanagement_team1.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.Locale;

@Getter
public class DataTypeException extends Exception{
    private String fieldName;

    public DataTypeException(String errorCode, MessageSource messageSource, String fieldName) {
        super(messageSource.getMessage(errorCode, null, Locale.getDefault()));
        this.fieldName = fieldName;
    }

    public DataTypeException(String errorCode, MessageSource messageSource) {
        super(messageSource.getMessage(errorCode, null, Locale.getDefault()));
    }

    public DataTypeException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }
}
