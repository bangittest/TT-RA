package com.example.warehousemanagement_team1.utils.validation;

import com.example.warehousemanagement_team1.exception.DataTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Validator {
    @Autowired
    private MessageSource messageSource;

    public Integer validateInteger(String id) throws DataTypeException {
        if (id.trim().isEmpty()) {
            throw new DataTypeException("SYSS-0011", messageSource);
        }
        try {
            return Integer.parseInt(id.trim());
        } catch (Exception e) {
            throw new DataTypeException("SYSS-0005", messageSource,id);
        }
    }

    public Map<Integer, Integer> validateMonthAndYear(String monthYear) throws DataTypeException {
        if (monthYear.trim().isEmpty()) {
            throw new DataTypeException("SYSS-0011", messageSource);
        }
        if(!monthYear.matches("^(0[1-9]|1[0-2])/\\d{4}$")){
            throw new DataTypeException("SYSS-0005", messageSource,monthYear);
        }
        Map<Integer, Integer> map = new HashMap<>();

        String[] date = monthYear.split("/");
        try {
            map.put(validateInteger(date[0]), validateInteger(date[1]));
            return map;
        } catch (Exception e) {
            throw new DataTypeException("SYSS-0005", messageSource,monthYear);
        }
    }
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
    public String validateString(String string) throws DataTypeException {
        if(string.isEmpty()||string.trim().isEmpty()){
            throw new DataTypeException("SYSS-0005", messageSource);
        }
        return string.trim();
    }
    public void validatePhoneNumber(String phoneNumber, String fieldName, List<String> validationErrors) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            validationErrors.add(fieldName + " không thể trống");
        } else if (!phoneNumber.matches("\\d{10}")) {
            validationErrors.add(fieldName + " Phải là một số 10 chữ số");
        }
    }

    public boolean checkDayBetween(LocalDate startDate,LocalDate endDate){
        Period period = Period.between(startDate, endDate);
        int days = period.getDays()+period.getMonths()*12+period.getYears()*365;
        return days < 15;
    }

}
