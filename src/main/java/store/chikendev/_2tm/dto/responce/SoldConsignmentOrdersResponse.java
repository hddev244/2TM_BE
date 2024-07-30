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
public class SoldConsignmentOrdersResponse {
    private Long orderId;
    private Long productId;
    private String customerId;
    private String customerName;
    private int quantitySold;
    private int quantityReturned;
    private Date saleDate;
    private Double totalSale;
    private Double totalAmount;
}
