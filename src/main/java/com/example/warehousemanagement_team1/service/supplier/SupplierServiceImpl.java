package com.example.warehousemanagement_team1.service.supplier;

import com.example.warehousemanagement_team1.dto.SupplierDTO;
import com.example.warehousemanagement_team1.model.Supplier;
import com.example.warehousemanagement_team1.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl implements SupplierService{
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private MessageSource messageSource;
    @Override
    public SupplierDTO save(SupplierDTO supplierDTO){
        Supplier supplier=supplierRepository.save(Supplier.builder()
                        .supplierName(supplierDTO.getSupplierName())
                        .phone(supplierDTO.getPhone())
                        .address(supplierDTO.getAddress())
                        .email(supplierDTO.getEmail())
                .build());
        return new SupplierDTO(supplier);
    }
}
