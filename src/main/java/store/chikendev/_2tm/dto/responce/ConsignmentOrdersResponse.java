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
public class ConsignmentOrdersResponse {

    private Long id;
    private Date createdAt;
    private String note;
    private String ordererName;
    private String deliveryPersonName;
    private String productName;
    private String storeName;
    private String stateName;
    private String address;
    private String phone;
    private String urlImage;
    private Date statusChangeDate;
}
