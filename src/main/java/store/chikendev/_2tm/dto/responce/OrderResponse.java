package store.chikendev._2tm.dto.responce;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Double deliveryCost;
    private String note;
    private Boolean paymentStatus;
    private String paymentId;
    private String consigneeDetailAddress;
    private String consigneeName;
    private String consigneePhoneNumber;
    private Double totalPrice;
    private String stateOrder;
    private String paymentMethod;
}
