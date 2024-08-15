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
@Entity
@Table(name = "statesOrder")
@Builder
@Data
public class StateOrder {
    public static final Long IN_CONFIRM = 1L;
    // cửa hàng xác nhận chờ vận chuyển
    public static final Long CONFIRMED = 12L;
    public static final Long CANCELLED_ORDER = 13L;
    public static final Long DELIVERING = 4L;
    public static final Long DELIVERED_SUCCESS = 5L;
    public static final Long ORDER_RETURN = 6L;
    public static final Long DELIVERED_FAIL = 9L;
    public static final Long RETURNED = 7L;
    public static final Long RETURNED_SUCCESS = 8L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String status;
    private String description;

    @OneToMany(mappedBy = "stateOrder")
    private List<Order> orders;
}
