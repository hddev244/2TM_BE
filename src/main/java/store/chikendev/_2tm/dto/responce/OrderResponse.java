package store.chikendev._2tm.dto.responce;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.chikendev._2tm.entity.StateOrder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private String storeName;
    private String paymentRecordId;
    private Boolean orderType;
    private List<OrderDetailResponse> detail;
    private StateOrderResponse orderState;
}
