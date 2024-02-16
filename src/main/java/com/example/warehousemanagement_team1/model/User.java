package com.example.warehousemanagement_team1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(columnDefinition = "varchar(50)", unique = true)
    private String username;
    @Column(columnDefinition = "varchar(200)")
    private String password;
    @Column(columnDefinition = "boolean default true")
    private Boolean status = true;
    @Column(columnDefinition = "varchar(50)")
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @OneToMany(mappedBy = "createdUser")
    @JsonIgnore
    private Set<Orders>orders;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id", referencedColumnName = "warehouseId")
    private Warehouse warehouse;

}
