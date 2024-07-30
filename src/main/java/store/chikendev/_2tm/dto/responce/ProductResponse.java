package store.chikendev._2tm.dto.responce;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.chikendev._2tm.entity.StateProduct;

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
    private CategoryResponse category;
    private StoreResponse store;
    private StateProduct state;
    private Boolean type;
    private List<ResponseDocumentDto> images;
    private ResponseDocumentDto thumbnail;
    private Long idCategory;
    private String typeProduct;
}
