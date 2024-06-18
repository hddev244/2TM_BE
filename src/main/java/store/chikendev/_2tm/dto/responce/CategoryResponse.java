package store.chikendev._2tm.dto.responce;

import lombok.Data;
import store.chikendev._2tm.entity.Product;

import java.util.List;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private List<Product> products;
}
