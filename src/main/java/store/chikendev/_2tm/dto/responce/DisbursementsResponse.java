package store.chikendev._2tm.dto.responce;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DisbursementsResponse {
    private Date payoutAt;
    private Boolean state;
    private Double commissionRate;
    private Long orderDetail;
    private String paymentClerk;
    private String paymentDisbursement;
}
