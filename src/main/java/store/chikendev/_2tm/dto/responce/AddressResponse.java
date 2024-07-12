package store.chikendev._2tm.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long id;
    private String fullAddress;
    private String streetAddress;
    private Long districtId;
    private Long provinceId;
    private Long wardId;
    private String phoneNumber;
}
