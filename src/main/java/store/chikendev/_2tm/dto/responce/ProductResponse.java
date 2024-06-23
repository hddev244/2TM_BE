package store.chikendev._2tm.dto.responce;

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
public class ProductResponse {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
    private String description;
    private List<AttributeProductResponse> attributes;
    private StoreResponse store;
    private List<ResponseDocumentDto> thumbnail;
    private Long idCategory;
}
