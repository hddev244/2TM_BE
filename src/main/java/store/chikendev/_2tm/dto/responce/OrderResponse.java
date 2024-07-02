package store.chikendev._2tm.dto.responce;

import java.util.Date;
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
    private Long id;
    private Double deliveryCost;
    private String note;
    private Date createdAt;
    private Date completeAt;
    private Boolean paymentStatus;
    private String paymentId;
    private String address;
    private String consigneeName;
    private String consigneePhoneNumber;
    private Double totalPrice;
    private String accountName;
    private String state;
    private String paymentMethodName;
    private List<OrderDetailResponse> detail;
}
