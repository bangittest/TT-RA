package com.example.warehousemanagement_team1.service.user;

import com.example.warehousemanagement_team1.dto.request.UserRequestLoginDTO;
import com.example.warehousemanagement_team1.dto.response.UserResponseLoginDTO;
import com.example.warehousemanagement_team1.exception.UserException;
import com.example.warehousemanagement_team1.model.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    UserResponseLoginDTO login(UserRequestLoginDTO userRequestLoginDTO) throws UserException;
    User getAccount(HttpServletRequest request);
}
