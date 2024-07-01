package store.chikendev._2tm.dto.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private Double deliveryCost;
    private String note;
    private Boolean paymentStatus;
    private String paymentId;
    private String consigneeDetailAddress;
    private String consigneeName;
    private String consigneePhoneNumber;
    private Double totalPrice;
    private Long stateOrder;
    private Long paymentMethod;
    private Long ward;
}
