package com.example.warehousemanagement_team1.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class JWTEntryPoint implements AuthenticationEntryPoint {
    private final Logger LOGGER = LoggerFactory.getLogger(JWTEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        LOGGER.error(authException.getMessage());
        ResponseEntity<String> responseEntity = new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        response.setStatus(responseEntity.getStatusCode().value());
        response.getWriter().write(Objects.requireNonNull(responseEntity.getBody()));
    }
}
