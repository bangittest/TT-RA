package com.example.warehousemanagement_team1.service.email;

import com.example.warehousemanagement_team1.model.Orders;

public interface EmailService {
    void sendEmailToSupplier(Orders order);
    void sendEmailToRecipient(Orders order);
}
