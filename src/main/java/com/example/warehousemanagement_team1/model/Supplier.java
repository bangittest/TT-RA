package com.example.warehousemanagement_team1.model;

import com.example.warehousemanagement_team1.dto.SupplierDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import javax.print.ServiceUI;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;
    @Column(columnDefinition = "varchar(50)")
    private String supplierName;
    @Column(columnDefinition = "varchar(50)")
    private String email;
    @Column(columnDefinition = "varchar(200)")
    private String address;
    @Column(columnDefinition = "varchar(11)")
    private String phone;

    @OneToMany(mappedBy = "supplier")
    @JsonIgnore
    private Set<Orders>orders;

    public Supplier(SupplierDTO supplierDTO) {
        this.supplierName = supplierDTO.getSupplierName();
        this.email = supplierDTO.getEmail();
        this.address = supplierDTO.getAddress();
        this.phone = supplierDTO.getPhone();
    }
}
