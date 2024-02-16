package com.example.warehousemanagement_team1.dto;

import com.example.warehousemanagement_team1.model.Supplier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.NumberFormat;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SupplierDTO {
    @NotEmpty(message = "{SYSS-0011}")
    @Size(max = 50, message = "{SYSS-0003}")
    private String supplierName;

    @NotEmpty(message = "{SYSS-0011}")
    @Size(max = 50, message = "{SYSS-0003}")
    @Email(message = "{SYSS-0005}")
    private String email;

    @NotEmpty(message = "{SYSS-0011}")
    @Size(max = 200, message = "{SYSS-0003}")
    private String address;

    @NotEmpty(message = "{SYSS-0011}")
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    @Size(min = 10,message = "{SYSS-0006}")
    @Size(max = 11,message = "{SYSS-0007}")
    private String phone;

    public SupplierDTO(Supplier supplier) {
        this.supplierName = supplier.getSupplierName();
        this.email = supplier.getEmail();
        this.address = supplier.getAddress();
        this.phone = supplier.getPhone();
    }
}
