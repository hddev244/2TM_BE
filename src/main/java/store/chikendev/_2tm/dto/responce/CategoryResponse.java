package store.chikendev._2tm.dto.responce;

import lombok.Data;
import store.chikendev._2tm.entity.Image;
import store.chikendev._2tm.entity.Product;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private ResponseDocumentDto   image; 
    // private List<Product> products;
}
