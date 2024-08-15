package store.chikendev._2tm.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
public class ShippingCost {
    public static final Long IN_THE_WARD = 1L;
    public static final Long IN_THE_DISTRICT = 2L;
    public static final Long OUTSIDE_THE_DISTRICT = 3L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double cost;
}
