package store.chikendev._2tm.dto.responce;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class OrderDetailResponse {
    private Long id;
    private Double price;
    private Integer quantity;
    private ProductResponse product;
}
