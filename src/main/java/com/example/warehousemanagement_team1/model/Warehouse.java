package com.example.warehousemanagement_team1.model;

import com.example.warehousemanagement_team1.dto.request.WarehouseRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Warehouse {
    @Id
    @Column(columnDefinition = "varchar(5)")
    private String warehouseId;
    @Column(columnDefinition = "varchar(50)", unique = true)
    private String warehouseName;
    @Column(columnDefinition = "varchar(200)")
    private String address;
    @Column(columnDefinition = "decimal(17,15)")
    private Double latitude;
    @Column(columnDefinition = "decimal(18,15)")
    private Double longitude;
    private Integer capacity;
    @Column(columnDefinition = "integer default 0")
    private Integer stock;

    @OneToMany(mappedBy = "warehouse")
    @JsonIgnore
    private Set<Orders>orders;

    @OneToMany(mappedBy = "warehouse")
    @JsonIgnore
    private Set<OrderHistory>orderHistories;

    public Warehouse(WarehouseRequestDTO warehouseRequestDTO) {
        this.warehouseId = warehouseRequestDTO.getWarehouseId();
        this.warehouseName = warehouseRequestDTO.getWarehouseName();
        this.address = warehouseRequestDTO.getAddress();
        this.latitude = warehouseRequestDTO.getLatitude();
        this.longitude = warehouseRequestDTO.getLongitude();
        this.capacity = warehouseRequestDTO.getCapacity();
        this.stock = warehouseRequestDTO.getStock();
    }
}
