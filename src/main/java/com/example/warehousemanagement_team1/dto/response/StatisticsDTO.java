package com.example.warehousemanagement_team1.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StatisticsDTO {
    private String orders;
    private List<Integer> values;
}
