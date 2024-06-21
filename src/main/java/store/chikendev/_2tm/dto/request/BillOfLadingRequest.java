package store.chikendev._2tm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillOfLadingRequest {
    private Long order;
    private String deliveryPerson;
    private String createBy;
}
