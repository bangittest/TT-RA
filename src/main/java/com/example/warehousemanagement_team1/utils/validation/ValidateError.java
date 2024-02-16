package com.example.warehousemanagement_team1.utils.validation;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ValidateError {
    private int rowIndex;
    private int columnIndex;
    private String fieldName;
    private String errorMessage;
}
