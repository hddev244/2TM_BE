package store.chikendev._2tm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestProduct {
    private String name;
    private Double price;
    private Integer quantity;
    private String description;
    private String accountId;
    private Long storeId;

    // Constructors, getters, and setters
}
