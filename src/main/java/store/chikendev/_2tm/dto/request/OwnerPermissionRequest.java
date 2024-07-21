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
public class OwnerPermissionRequest {
    private String bankName;
    private String BankAccountNumber;
    private String accountHolderName;
}
