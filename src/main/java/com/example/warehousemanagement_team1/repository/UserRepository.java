package com.example.warehousemanagement_team1.repository;

import com.example.warehousemanagement_team1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);
}
