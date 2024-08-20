package store.chikendev._2tm.dto.responce;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nimbusds.jose.shaded.gson.annotations.JsonAdapter;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductReportResponse {
     @Id
    private Long id;
    private ProductResponse product;
    private Long totalOrder;
    private Long totalQuantity;
    private Double totalRevenue;

    public ProductReportResponse(ProductResponse product, Long totalQuantity, Double totalRevenue) {
        this.product = product;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }
}
