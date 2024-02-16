package com.example.warehousemanagement_team1.service.warehouse;

import com.example.warehousemanagement_team1.dto.request.WarehouseRequestDTO;
import com.example.warehousemanagement_team1.dto.response.WarehouseResponseDTO;
import com.example.warehousemanagement_team1.exception.WarehouseException;
import com.example.warehousemanagement_team1.model.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {
    List<WarehouseResponseDTO> save(List<WarehouseRequestDTO> list) throws WarehouseException;

    Warehouse findById(String warehouseId) throws WarehouseException;
    Warehouse update(WarehouseRequestDTO warehouseRequestDTO) throws WarehouseException;

    WarehouseResponseDTO save(WarehouseRequestDTO warehouseRequestDTO) throws WarehouseException;

    Page<WarehouseResponseDTO> searchAllAndSortAndPage(Pageable pageable, String search);
}
