package com.example.warehousemanagement_team1.repository;

import com.example.warehousemanagement_team1.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier,String> {
    Supplier findByPhone(String phone);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
}
