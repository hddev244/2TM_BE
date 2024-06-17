package store.chikendev._2tm.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.chikendev._2tm.entity.ProvinceCity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceCityResponse {
    private Long id;
    private String name;
}
