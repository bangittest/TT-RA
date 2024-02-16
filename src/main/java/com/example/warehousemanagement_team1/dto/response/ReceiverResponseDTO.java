package com.example.warehousemanagement_team1.dto.response;

import com.example.warehousemanagement_team1.model.Receiver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReceiverResponseDTO {
    private String receiverName;
    private String email;
    private String address;
    private String phone;

    public ReceiverResponseDTO(Receiver receiver) {
        this.receiverName = receiver.getReceiverName();
        this.email = receiver.getEmail();
        this.address = receiver.getAddress();
        this.phone = receiver.getPhone();
    }
}
