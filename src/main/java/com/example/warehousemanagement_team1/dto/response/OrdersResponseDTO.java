package com.example.warehousemanagement_team1.dto.response;

import com.example.warehousemanagement_team1.model.Orders;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OrdersResponseDTO {
    private String orderId;
    private LocalDateTime createdAt;
    private Integer status;
    private String warehouseId;
    private String warehouseName;

    private String supplierName;
    private String supplierAddress;
    private String supplierPhone;
    private String supplierEmail;

    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private String receiverEmail;
    private LocalDateTime storedAt;

    private LocalDateTime deliveredAt;
    private Integer numberOfFailedDelivery;
    private LocalDateTime returnedAt;

    public OrdersResponseDTO(Orders orders) {
        this.orderId = orders.getOrderId();
        this.createdAt = orders.getCreatedAt();
        this.status = orders.getStatus();
        this.warehouseId =orders.getWarehouse().getWarehouseId();
        this.warehouseName = orders.getWarehouse().getWarehouseName();
        this.supplierName = orders.getSupplier().getSupplierName();
        this.supplierAddress = orders.getSupplier().getAddress();
        this.supplierPhone = orders.getSupplier().getPhone();
        this.supplierEmail = orders.getSupplier().getEmail();
        this.receiverName = orders.getReceiver().getReceiverName();
        this.receiverAddress = orders.getReceiver().getAddress();
        this.receiverPhone = orders.getReceiver().getPhone();
        this.receiverEmail = orders.getReceiver().getEmail();
        this.storedAt = orders.getStoredAt();
        this.deliveredAt = orders.getDeliveredAt();
        this.numberOfFailedDelivery = orders.getNumberOfFailedDelivery();
        this.returnedAt = orders.getReturnedAt();
    }
}
