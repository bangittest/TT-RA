package com.example.warehousemanagement_team1.security.config;

import com.example.warehousemanagement_team1.security.jwt.AccessDenied;
import com.example.warehousemanagement_team1.security.jwt.JWTEntryPoint;
import com.example.warehousemanagement_team1.security.jwt.JWTTokenFilter;
import com.example.warehousemanagement_team1.security.user_principle.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private JWTEntryPoint jwtEntryPoint;

    @Autowired
    private JWTTokenFilter jwtTokenFilter;
    @Autowired
    private AccessDenied accessDenied;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable).authenticationProvider(authenticationProvider())
                //Cấu hình xác thực và phân quyền truy cập
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/login","/api/warehouses/**","/**").permitAll()
                            .requestMatchers("/api/orders/**").hasAuthority("USER")
//                            .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
//                            .requestMatchers("/api/orders/**").hasAuthority("ADMIN")
                            .anyRequest().authenticated();
                })
                //Xử lý ngoại lệ
                .exceptionHandling(auth ->
                        auth.authenticationEntryPoint(jwtEntryPoint)
                                .accessDeniedHandler(accessDenied))

                //cấu hình quyền quản lý phiên làm việc trong một ứng dụng web (phi trang thai)
                .sessionManagement(auth -> auth.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}
