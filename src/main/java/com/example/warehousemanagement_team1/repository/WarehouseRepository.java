package com.example.warehousemanagement_team1.repository;

import com.example.warehousemanagement_team1.model.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, String> {
    Warehouse findByWarehouseName(String warehouseName);

    @Query("select w from Warehouse w where w.stock<w.capacity")
    List<Warehouse> findAvailableWarehouse();

    Page<Warehouse> findAllByWarehouseIdContainsIgnoreCaseOrWarehouseNameContainsIgnoreCase(Pageable pageable, String wareHouseId, String warehouseName);

    Warehouse findByWarehouseIdIgnoreCase(String warehouseId);
}
