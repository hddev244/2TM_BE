package store.chikendev._2tm.dto.responce;

import java.util.Date;
import java.util.List;

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
public class AccountResponse {
    private String id;
    private String username;
    private String fullName;
    private Integer violationPoints;
    private String phoneNumber;
    private String address;
    private String email;
    private List<RoleResponse> roles;
    private List<CartResponse> cartItems;
    private Date createdAt;
    private Date updatedAt;
    private String stateName;
    private ResponseDocumentDto image;
    private AddressResponse primaryAddress;
    private StoreResponse store;
}
