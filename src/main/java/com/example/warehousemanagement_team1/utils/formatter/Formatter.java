package com.example.warehousemanagement_team1.utils.formatter;

import com.example.warehousemanagement_team1.exception.DataTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@Component
public class Formatter {
    @Autowired
    private MessageSource messageSource;
    public String formatDate() throws DataTypeException {
        String dateFormat = "yyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            return simpleDateFormat.format(new Date());
        } catch (Exception e) {
            throw new DataTypeException("SYSS-0005",messageSource);
        }
    }

    public String formatDate(LocalDateTime date) throws DataTypeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            return date.format(formatter);
        } catch (Exception e) {
            throw new DataTypeException("SYSS-0005",messageSource,date.toString());
        }
    }

    public String formatDateTime(LocalDateTime dateTime) throws DataTypeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        try {
            return dateTime.format(formatter);
        } catch (DateTimeParseException dateTimeParseException) {
            throw new DataTypeException("SYSS-0005",messageSource,dateTime.toString());
        }
    }

    public String formatTime(LocalDateTime dateTime) throws DataTypeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            return dateTime.format(formatter);
        } catch (DateTimeParseException dateTimeParseException) {
            throw new DataTypeException("SYSS-0005",messageSource,dateTime.toString());
        }
    }

    public LocalDate convertStringToLocalDate(String dateString) throws DataTypeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException dateTimeParseException) {
            throw new DataTypeException("SYSS-0005",messageSource,dateString);
        }
    }

    public LocalDate convertToLocalDate(String dateString) throws DataTypeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        try {
            return YearMonth.parse(dateString, formatter).atDay(1);
        } catch (DateTimeParseException dateTimeParseException) {
            throw new DataTypeException("SYSS-0005",messageSource,dateString);
        }
    }

}
