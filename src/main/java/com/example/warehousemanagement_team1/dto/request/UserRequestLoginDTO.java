package com.example.warehousemanagement_team1.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRequestLoginDTO {
    @NotEmpty(message = "{SYSS-0011}")
    private String username;

    @NotEmpty(message = "{SYSS-0011}")
    @Size(min = 4,message = "{SYSS-0006}")
    @Size(max = 8,message = "{SYSS-0007}")
    private String password;

    @NotEmpty(message = "{SYSS-0011}")
    private String warehouseId;
}
