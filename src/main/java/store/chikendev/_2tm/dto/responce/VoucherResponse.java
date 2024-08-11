package store.chikendev._2tm.dto.responce;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherResponse {
    private String id;
    private Double DiscountPercentage;
    private String note;
    private int UsageLimit;
    private Boolean status;
    private Date startDate;
    private Date endDate;
    private Double MinimumPurchaseAmount;
    private Double MaximumDiscountAmount;
    private List<OrderResponse> orders;
}
