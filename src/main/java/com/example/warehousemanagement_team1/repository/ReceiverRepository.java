package com.example.warehousemanagement_team1.repository;

import com.example.warehousemanagement_team1.model.Receiver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiverRepository extends JpaRepository<Receiver,String> {
    Receiver findByPhone(String phone);
    Boolean existsReceiverByEmail(String email);
    Boolean existsReceiverByPhone(String phone);
}
