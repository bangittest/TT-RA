package com.example.warehousemanagement_team1.model;

import com.example.warehousemanagement_team1.dto.request.ReceiverRequestDTO;
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
public class Receiver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiverId;
    @Column(columnDefinition = "varchar(50)")
    private String receiverName;
    @Column(columnDefinition = "varchar(50)")
    private String email;
    @Column(columnDefinition = "varchar(200)")
    private String address;
    @Column(columnDefinition = "varchar(11)")
    private String phone;
    @Column(columnDefinition = "decimal(17,15)")
    private Double latitude;
    @Column(columnDefinition = "decimal(18,15)")
    private Double longitude;

    @OneToMany(mappedBy = "receiver")
    @JsonIgnore
    private Set<Orders> orders;

    public Receiver(ReceiverRequestDTO receiverDTO) {
        this.receiverName = receiverDTO.getReceiverName();
        this.email = receiverDTO.getEmail();
        this.address = receiverDTO.getAddress();
        this.phone = receiverDTO.getPhone();
        this.latitude = receiverDTO.getLatitude();
        this.longitude = receiverDTO.getLongitude();
    }
}
