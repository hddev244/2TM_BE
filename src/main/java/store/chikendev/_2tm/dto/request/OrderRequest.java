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
    private String consigneeDetailAddress;
    private String consigneeName;
    private String consigneePhoneNumber;
    private Long paymentMethodId;
    private Long wardId;
}
