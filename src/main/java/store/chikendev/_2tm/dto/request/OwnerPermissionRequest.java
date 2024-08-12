package store.chikendev._2tm.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerPermissionRequest {
    @NotBlank(message = "bankName is required")
    private String bankName;
    @NotBlank(message = "BankAccountNumber is required")
    private String bankAccountNumber;
    @NotBlank(message = "accountHolderName is required")
    private String accountHolderName;
}
