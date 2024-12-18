package store.chikendev._2tm.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreResponse {
    private Long id;
    private String name;
    private String postalCode;
    private String phone;
    private String email;
    private String streetAddress;
    private String description;
    private ResponseDocumentDto urlImage;
}
