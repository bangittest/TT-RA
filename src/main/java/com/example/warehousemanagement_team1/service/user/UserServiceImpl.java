package com.example.warehousemanagement_team1.service.user;

import com.example.warehousemanagement_team1.dto.request.UserRequestLoginDTO;
import com.example.warehousemanagement_team1.dto.response.UserResponseLoginDTO;
import com.example.warehousemanagement_team1.exception.UserException;
import com.example.warehousemanagement_team1.model.User;
import com.example.warehousemanagement_team1.repository.UserRepository;
import com.example.warehousemanagement_team1.security.jwt.JWTProvider;
import com.example.warehousemanagement_team1.security.jwt.JWTTokenFilter;
import com.example.warehousemanagement_team1.security.user_principle.UserPrinciple;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private JWTProvider jwtProvider;
    @Autowired
    private JWTTokenFilter jwtTokenFilter;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private MessageSource messageSource;

    @Override
    public UserResponseLoginDTO login(UserRequestLoginDTO userRequestLoginDTO) throws UserException {
        Authentication authentication;
        authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userRequestLoginDTO.getUsername(), userRequestLoginDTO.getPassword()));

        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        if (userPrinciple == null) {
            throw new UserException("SYSS-2000", messageSource);
        }
        User user = userRepository.findByUsername(userRequestLoginDTO.getUsername());
        if (!user.getWarehouse().getWarehouseId().equalsIgnoreCase(userRequestLoginDTO.getWarehouseId())) {
            throw new UserException("SYSS-2001", messageSource);
        }

//        // check truong hop khong phai admin thi phai nhap ma kho hang
//        if (!user.getUsername().equals("admin")) {
//            if (!user.getWarehouse().getWarehouseId().equalsIgnoreCase(userRequestLoginDTO.getWarehouseId())) {
//                throw new UserException("SYSS-2001", messageSource);
//            }
//        }
        //
        return UserResponseLoginDTO.builder()
                .token(jwtProvider.generateToken(userPrinciple))
                .username(userPrinciple.getUsername())
                .build();
    }

    @Override
    public User getAccount(HttpServletRequest request) {
        String token = jwtTokenFilter.getTokenFromRequest(request);
        if (token != null) {
            String username = jwtProvider.getUsernameToken(token);
            return userRepository.findByUsername(username);
        }

        return null;
    }
}
