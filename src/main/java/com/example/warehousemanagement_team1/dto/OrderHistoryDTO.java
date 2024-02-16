package com.example.warehousemanagement_team1.dto;

import com.example.warehousemanagement_team1.model.OrderHistory;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderHistoryDTO {
    private LocalDateTime orderedAt;
    private Integer status;
    private String reason;

    public OrderHistoryDTO(OrderHistory orderHistory) {
        this.orderedAt = orderHistory.getOrderedAt();
        this.status = orderHistory.getStatus();
        this.reason = orderHistory.getReason().getDescription();
    }
}
