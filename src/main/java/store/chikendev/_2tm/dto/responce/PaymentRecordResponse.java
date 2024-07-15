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
public class PaymentRecordResponse {
    private String id;
    private Double amount;
    private String bankCode;
    private String cartType;
    private String bankTranNo;
    private Date payDate;
    private String status;
    private AccountResponse account;
    private Date createdAt;
    private Date updatedAt;

}
