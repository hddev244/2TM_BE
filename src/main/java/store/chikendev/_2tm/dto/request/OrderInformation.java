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
public class OrderInformation {
    private String note;
    @NotBlank(message = "Không bỏ trống địa chỉ giao hàng chi tiết")
    private String consigneeDetailAddress;
    @NotBlank(message = "Không bỏ trống tên tên người nhận")
    private String consigneeName;
    @NotBlank(message = "Không bỏ trống số điện thoại người nhận")
    @Pattern(regexp = "^(\\+84|0)[1-9][0-9]{8}$", message = "Số điện thoại không hợp lệ")
    private String consigneePhoneNumber;
    private Long paymentMethodId;
    @NotNull(message = "Không bỏ trống quận, huyện nơi giao hàng")
    private Long wardId;
    private List<OrderRequest> details;
}
