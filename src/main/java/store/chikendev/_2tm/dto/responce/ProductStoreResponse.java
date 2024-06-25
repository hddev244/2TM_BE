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
public class ProductStoreResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String streetAddress;
    private String description;
    private Date createdAt;
    private List<ProductResponse> products;
    private ResponseDocumentDto urlImage;
}
