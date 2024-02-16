package com.example.warehousemanagement_team1.dto.request;

import com.example.warehousemanagement_team1.dto.SupplierDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequestDTO {
    @Size(max = 15, message = "{SYSS-0003}")
    private String orderId;

    private LocalDateTime createdAt=LocalDateTime.now();
    private Integer status=0;

    @Valid
    private SupplierDTO supplier;

    @Valid
    private ReceiverRequestDTO receiver;

    private Integer numberOfFailedDelivery=0;

}
