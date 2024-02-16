package com.example.warehousemanagement_team1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Reason {
    @Id
//    @Column(columnDefinition = "varchar(3)")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reasonId;

    @Column(columnDefinition = "varchar(200)")
    private String description;


    @OneToMany(mappedBy = "reason")
    @JsonIgnore
    private Set<OrderHistory>orderHistories;
}
