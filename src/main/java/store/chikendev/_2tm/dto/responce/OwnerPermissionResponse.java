package store.chikendev._2tm.dto.responce;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OwnerPermissionResponse {
    private Long id;
    private String bankName;
    private String BankAccountNumber;
    private String accountHolderName;
    private String state;
    private Date createdAt;
    private AccountResponse accountResponse;
}
