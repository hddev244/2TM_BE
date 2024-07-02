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
    private AccountResponse orderer;
    private AccountResponse deliveryPerson;
    private ProductResponse product;
    private StoreResponse store;
    private String stateName;
    private String address;
    private String phone;
    private ResponseDocumentDto image;
    private Date statusChangeDate;
}
