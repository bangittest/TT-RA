package com.example.warehousemanagement_team1.service.supplier;

import com.example.warehousemanagement_team1.dto.SupplierDTO;
import com.example.warehousemanagement_team1.exception.SupplierException;

public interface SupplierService {
    SupplierDTO save(SupplierDTO supplierDTO) throws SupplierException;
}
