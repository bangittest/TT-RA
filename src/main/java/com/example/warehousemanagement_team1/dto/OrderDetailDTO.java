package com.example.warehousemanagement_team1.dto;

import com.example.warehousemanagement_team1.dto.response.ReceiverResponseDTO;
import com.example.warehousemanagement_team1.dto.response.WarehouseResponseDTO;
import com.example.warehousemanagement_team1.model.OrderHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class OrderDetailDTO {
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
    private Set<OrderHistoryDTO> orderHistories;
}
