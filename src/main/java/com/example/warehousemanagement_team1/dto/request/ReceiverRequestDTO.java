package com.example.warehousemanagement_team1.dto.request;

import com.example.warehousemanagement_team1.model.Receiver;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.NumberFormat;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReceiverRequestDTO {
    @NotEmpty(message = "{SYSS-0011}")
    @Size(max = 50, message = "{SYSS-0003}")
    private String receiverName;

    @NotEmpty(message = "{SYSS-0011}")
    @Size(max = 50, message = "{SYSS-0003}")
    @Email(message = "{SYSS-0005}")
    private String email;

    @NotEmpty(message = "{SYSS-0011}")
    @Size(max = 200, message = "{SYSS-0003}")
    private String address;

    @NotEmpty(message = "{SYSS-0011}")
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    @Size(min = 10,message = "{SYSS-0006}")
    @Size(max = 11,message = "{SYSS-0007}")
    private String phone;

    @NotNull(message = "{SYSS-0011}")
    @DecimalMin(value = "-90.0", inclusive = true,message = "{SYSS-0006}")
    @DecimalMax(value = "90.0", inclusive = true,message = "{SYSS-0007}")
//    @Pattern(regexp = "^\\d{0,2}(\\.\\d{0,15})?$", message = "{SYSS-0005}")
    private Double latitude;

    @NotNull(message = "{SYSS-0011}")
    @DecimalMin(value = "-180.0", inclusive = true,message = "{SYSS-0006}")
    @DecimalMax(value = "180.0", inclusive = true,message = "{SYSS-0007}")
//    @Pattern(regexp = "^\\d{0,3}(\\.\\d{0,15})?$", message = "{SYSS-0005}")
    private Double longitude;

    public ReceiverRequestDTO(Receiver receiver) {
        this.receiverName = receiver.getReceiverName();
        this.email = receiver.getEmail();
        this.address = receiver.getAddress();
        this.phone = receiver.getPhone();
        this.latitude = receiver.getLatitude();
        this.longitude = receiver.getLongitude();
    }
}
