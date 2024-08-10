package store.chikendev._2tm.dto.responce;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillOfLadingResponse {
    private Long id;
    private String deliveryPerson;
    private String createBy;
    private Date createdAt;
    private String urlImage;
    private Double totalAmount;
    private AccountResponse orderer;
    private OrderResponse orderId;
}
