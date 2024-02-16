package com.example.warehousemanagement_team1.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequestConfirmDTO {
    @NotEmpty(message = "{SYSS-0011}")
    private String orderId;
    @NotEmpty(message = "{SYSS-0011}")
    private String statusOrder;
    private String reasonId;
}
