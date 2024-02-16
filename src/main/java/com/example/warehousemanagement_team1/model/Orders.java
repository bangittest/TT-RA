package com.example.warehousemanagement_team1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Orders {
    @Id
    @Column(columnDefinition = "varchar(20)")
    private String orderId;
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
    @Column(columnDefinition = "tinyint default 0")
    private Integer status=0;

   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "supplier_id",referencedColumnName = "supplierId")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id",referencedColumnName = "receiverId")
    private Receiver receiver;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_user_id",referencedColumnName = "userId")
    private User createdUser;
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime storedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id",referencedColumnName = "warehouseId")
    private Warehouse warehouse;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime deliveredAt;
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime returnedAt;
    @Column(columnDefinition = "integer(1) default 0")
    private Integer numberOfFailedDelivery=0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reason_id",referencedColumnName = "reasonId")
    private Reason reason;

    @OneToMany(mappedBy = "order")
    @JsonIgnore
    private Set<OrderHistory>orderHistories;
}
