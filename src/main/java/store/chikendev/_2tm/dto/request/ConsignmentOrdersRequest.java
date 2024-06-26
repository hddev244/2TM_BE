package store.chikendev._2tm.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsignmentOrdersRequest {
    @NotBlank(message = "Không trống tên sản phẩm")
    private String name;
    @NotNull(message = "Không bỏ trống giá sản phẩm")
    private Double price;
    @NotNull(message = "Không bỏ trống số lượng sản phẩm")
    private Integer quantity;
    private String description;
    private Long idCategory;
    private List<Long> idAttributeDetail;

    private String note;
    private Long storeId;
    @NotBlank(message = "Không bỏ số điện thoại")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;
    private String detailAddress;
    private Long wardId;
}
