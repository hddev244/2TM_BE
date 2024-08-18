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
    public static final Long IN_CONFIRM = 1L; // Đơn hàng đang chờ cửa hàng xác nhận và tạo vận
    // cửa hàng xác nhận chờ vận chuyển
    public static final Long CONFIRMED = 2L; // đơn đã được cửa hàng xác nhận và tạo vận đơn, nhưng NVGH chưa lấy
    public static final Long ON_REFECT = 4L; // khách hàng từ chối nhận hàng, đang vận chuyển về lại cửa hàng.
    public static final Long CANCELLED_ORDER = 13L; // Đơn hàng đã bị hủy
    public static final Long DELIVERING = 3L; // Đơn hàng đã được nhân viên lấy đi giao - trên dường giao cho khách hàng
    public static final Long DELIVERED_SUCCESS = 5L; // Người mua đã nhận hàng - giao hàng thành công
    public static final Long ORDER_RETURN = 6L; // Đơn hàng đang đươc vẫn chuyển về cho cửa hàng
    public static final Long DELIVERED_FAIL = 9L;
    public static final Long RETURNED = 7L;
    public static final Long RETURNED_SUCCESS = 8L;
    public static final Long REFUSE = 14L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String status;
    private String description;

    @OneToMany(mappedBy = "stateOrder")
    private List<Order> orders;
}
