package store.chikendev._2tm.dto.responce;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.chikendev._2tm.entity.StateAccount;

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
    private Date createdAt;
    private Date updatedAt;
    private String stateName;
    private ResponseDocumentDto image;
    private StateAccount state;
}
