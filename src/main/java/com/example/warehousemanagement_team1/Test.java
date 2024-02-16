package com.example.warehousemanagement_team1;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


@Component
public class Test {

    public static void main(String[] args) {

        System.out.println(passwordEncoder("1234"));
        System.out.println(formatTime(LocalDateTime.now()));
        System.out.println(convertToLocalDate("10/2023"));
    }

    public static String formatTime(LocalDateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }

    private static String passwordEncoder(String number) {
        int strength = 10; // work factor of bcrypt
        BCryptPasswordEncoder bCryptPasswordEncoder =
                new BCryptPasswordEncoder(strength, new SecureRandom());
        return bCryptPasswordEncoder.encode(number);
    }
    public static LocalDate convertToLocalDate(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        try {
            return LocalDate.parse(dateString,formatter);
        } catch (DateTimeParseException dateTimeParseException) {
//            throw new DataTypeException("SYSS-0005"+dateString);
            System.out.println("sai");
            return null;
        }

    }

}
