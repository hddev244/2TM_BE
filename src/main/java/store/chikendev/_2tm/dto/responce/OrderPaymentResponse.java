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
public class OrderPaymentResponse {
    private List<OrderResponse> orders;
    private String paymentLink;
    private Long sumTotalPrice;
}
