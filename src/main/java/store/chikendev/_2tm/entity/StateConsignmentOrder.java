package store.chikendev._2tm.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "stateConsignmentOrder")
@Builder
public class StateConsignmentOrder {

    public static final Long IN_CONFIRM = 1L;
    public static final Long CONFIRM = 2L;
    public static final Long REFUSE = 6L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String status;
    private String description;

    @OneToMany(mappedBy = "stateId")
    private List<ConsignmentOrders> consignmentOrders;
}
