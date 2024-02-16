package com.example.warehousemanagement_team1.advice;

import com.example.warehousemanagement_team1.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> invalidRequest(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.put(fieldError.getField(), fieldError.getDefaultMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserException.class)
    public Map<String,String> userException(UserException userException){
        Map<String,String>stringMap=new HashMap<>();
        stringMap.put("errorMessage", userException.getMessage());
        return stringMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WarehouseException.class)
    public Map<String,String> warehouseException(WarehouseException warehouseException) {
        Map<String,String>stringMap=new HashMap<>();
        stringMap.put("errorMessage",warehouseException.getMessage());
        return stringMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataTypeException.class)
    public Map<String,String> dataTypeException(DataTypeException dataTypeException){
        Map<String,String>stringMap=new HashMap<>();
        stringMap.put("errorMessage",dataTypeException.getMessage());
        return stringMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ReasonException.class)
    public Map<String,String>reasonException(ReasonException reasonException){
        Map<String,String>stringMap=new HashMap<>();
        stringMap.put("errorMessage",reasonException.getMessage());
        return stringMap;
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exceptions.class)
    public Map<String,String> exceptions(ReasonException reasonException) {
        Map<String,String>eMap=new HashMap<>();
        eMap.put("errorMessage",reasonException.getMessage());
        return eMap;
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ResponseException.class)
    public Map<String,String>responseException(ResponseException responseException){
        Map<String,String>errorMap = new HashMap<>();
        errorMap.put("errorMessage",responseException.getMessage());
        return errorMap;
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OrderException.class)
    public Map<String,String>handleBusinessException(OrderException orderException){
        Map<String,String>errorMap = new HashMap<>();
        errorMap.put("errorMessage",orderException.getMessage());
        return errorMap;
    }

}
