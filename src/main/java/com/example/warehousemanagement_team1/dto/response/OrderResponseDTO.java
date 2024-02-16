package com.example.warehousemanagement_team1.dto.response;

import com.example.warehousemanagement_team1.dto.request.ReceiverRequestDTO;
import com.example.warehousemanagement_team1.dto.SupplierDTO;
import com.example.warehousemanagement_team1.model.*;
import com.example.warehousemanagement_team1.utils.formatter.Formatter;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderResponseDTO {
    private String orderId;
    private LocalDateTime createdAt;
    private Integer status;

    private SupplierDTO supplier;

    private ReceiverResponseDTO receiver;

    private Long createdUser;
    private LocalDateTime storedAt;

    private WarehouseResponseDTO warehouse;

    private LocalDateTime deliveredAt;
    private LocalDateTime returnedAt;
    private Integer numberOfFailedDelivery;

//    @JsonIgnore
//    private String reason;

    public OrderResponseDTO(Orders order) {
        this.orderId = order.getOrderId();
        this.createdAt = order.getCreatedAt();
        this.status = order.getStatus();
        this.supplier = new SupplierDTO(order.getSupplier());
        this.receiver = new ReceiverResponseDTO(order.getReceiver());
        this.createdUser = order.getCreatedUser().getUserId();
        this.storedAt = order.getStoredAt();
        this.warehouse = new WarehouseResponseDTO(order.getWarehouse());
        this.deliveredAt = order.getDeliveredAt();
        this.returnedAt = order.getReturnedAt();
        this.numberOfFailedDelivery = order.getNumberOfFailedDelivery();
//        this.reason = order.getReason().getDescription();
    }
}
