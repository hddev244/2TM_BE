package store.chikendev._2tm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailsRequest {
    private Double price;
    private Integer quantity;
    private Long product;
    private Long order;

}
