package com.example.warehousemanagement_team1.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ErrorDTO {
    private Integer row;
    private String message;
}
