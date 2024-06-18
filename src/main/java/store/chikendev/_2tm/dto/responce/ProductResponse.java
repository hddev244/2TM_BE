package store.chikendev._2tm.dto.responce;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
    private String description;
}
