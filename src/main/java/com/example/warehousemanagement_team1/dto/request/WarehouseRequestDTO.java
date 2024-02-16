package com.example.warehousemanagement_team1.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class WarehouseRequestDTO {
    private String warehouseId;

    @NotEmpty(message = "{SYSS-0011}")
    @Size(max = 50, message = "{SYSS-0003}")
    private String warehouseName;

    @NotEmpty(message = "{SYSS-0011}")
    @Size(max = 200, message = "{SYSS-0003}")
    private String address;


    @NotNull(message = "{SYSS-0011}")
    @DecimalMin(value = "-90.0", inclusive = true,message = "{SYSS-0006}")
    @DecimalMax(value = "90.0", inclusive = true,message = "{SYSS-0007}")
    @Digits(integer = 2, fraction = 15)
    private Double latitude;

    @NotNull(message = "{SYSS-0011}")
    @DecimalMin(value = "-180.0", inclusive = true,message = "{SYSS-0006}")
    @DecimalMax(value = "180.0", inclusive = true,message = "{SYSS-0007}")
    @Digits(integer = 3, fraction = 15)
    private Double longitude;

    @NotNull(message = "{SYSS-0011}")
    @Positive(message = "{SYSS-3001}")
    private Integer capacity;
    private Integer stock = 0;
}
