package com.example.warehousemanagement_team1.controller ;

import com.example.warehousemanagement_team1.dto.request.UserRequestLoginDTO;
import com.example.warehousemanagement_team1.dto.response.UserResponseLoginDTO;
import com.example.warehousemanagement_team1.exception.UserException;
import com.example.warehousemanagement_team1.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;

@RestController
@RequestMapping("/api/")
public class AuthController {
    @Autowired
    private UserService userService;
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequestLoginDTO userRequestDTO) throws UserException {
        UserResponseLoginDTO userResponseDTO= userService.login(userRequestDTO);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
    }

}
