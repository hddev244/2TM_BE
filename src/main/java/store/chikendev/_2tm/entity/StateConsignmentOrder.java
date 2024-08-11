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

    public static final Long CREATED = 1L; // VẬN ĐƠN ĐƯỢC TẠO THÀNH CÔNG
    public static final Long ON_THE_WAY_PICK_UP = 2L; // ĐANG TRÊN ĐƯỜNG ĐẾN LẤY HÀNG
    public static final Long PICKED_UP = 3L; // NHÂN VIÊN ĐÃ LẤY HÀNG, ĐANG GIAO VỀ CỬA HÀNG
    public static final Long COMPLETED = 4L; // VẬN ĐƠN ĐÃ HOÀN THÀNH
    public static final Long PICK_UP_FAILED = 5L; // LẤY HÀNG THẤT BẠI
    public static final Long REFUSE = 6L; // Nhân viên từ chối xác nhận đơn ký gửi không đúng yêu cầu
    public static final Long RETURN = 7L; // Đơn hàng được trả lại
    public static final Long RETURN_SUCCESS = 8L; // Đơn hàng đã được trả lại thành công
    public static final Long CANCEL = 9L; // Hủy đơn bởi chủ hàng

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String status;
    private String description;

    @OneToMany(mappedBy = "stateId")
    private List<ConsignmentOrders> consignmentOrders;
}
