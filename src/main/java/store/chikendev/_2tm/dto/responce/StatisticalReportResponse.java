package store.chikendev._2tm.dto.responce;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticalReportResponse {
    private Long idProduct;
    private String nameProduct;
    private int quantitySold;
    private Double totalSale;
    private Double totalAmount;
    private Date saleDate;

}
