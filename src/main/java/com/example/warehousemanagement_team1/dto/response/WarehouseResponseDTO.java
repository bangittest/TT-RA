package com.example.warehousemanagement_team1.dto.response;

import com.example.warehousemanagement_team1.model.Warehouse;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class WarehouseResponseDTO {
    private String warehouseId;

    private String warehouseName;

    public WarehouseResponseDTO(Warehouse warehouse) {
        this.warehouseId = warehouse.getWarehouseId();
        this.warehouseName = warehouse.getWarehouseName();
    }
}
