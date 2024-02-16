//package com.example.warehousemanagement_team1.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Builder
//public class UserWareRole {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//    @ManyToOne
//    @JoinColumn(name = "warehouse_id")
//    private Warehouse warehouse;
//    @ManyToOne
//    @JoinColumn(name = "role_id")
//    private Role role;
//}
