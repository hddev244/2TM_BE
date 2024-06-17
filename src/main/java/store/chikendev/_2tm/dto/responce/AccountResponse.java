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
public class AccountResponse {
    private String id;
    private String username;
    private String fullName;
    private Integer violationPoints;
    private String phoneNumber;
    private String email;
    private Date createdAt;
    private Date updatedAt;
    private String stateName;
    private ResponseDocumentDto urlImage;
}
