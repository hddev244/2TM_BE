package store.chikendev._2tm.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherRequest {
    private String id;
    private Double discountPercentage;
    private String note;
    private int usageLimit;
    private Boolean status;
    private Date startDate;
    private Date endDate;
    private Double minimumPurchaseAmount;
    private Double maximumDiscountAmount;
}
