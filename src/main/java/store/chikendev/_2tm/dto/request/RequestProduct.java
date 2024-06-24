package store.chikendev._2tm.dto.request;

import java.util.List;

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
    private Long accountId;
    private Long storeId;
    private Long categoryId;
    private Long ownerId;
    private Long stateId;
    private List<Long> attributeDetailIds;
    private List<Long> imageIds;

    // Constructors, getters, and setters
}
