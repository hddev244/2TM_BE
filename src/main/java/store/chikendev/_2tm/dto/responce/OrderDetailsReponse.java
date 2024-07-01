package store.chikendev._2tm.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailsReponse {
    private Double price;
    private Integer quantity;
    private ProductResponse product;
    private Long order;
}
