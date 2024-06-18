package store.chikendev._2tm.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRequest {
    @NotBlank(message = "Không bỏ trống tên cửa hàng")
    private String name;
    @NotBlank(message = "Không bỏ trống mã bưu điện cửa hàng")
    @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Mã bưu chính không hợp lệ")
    private String postalCode;
    @NotBlank(message = "Không bỏ trống số điện thoại cửa hàng")
    @Pattern(regexp = "^(\\+84|0)[1-9][0-9]{8}$", message = "Số điện thoại không hợp lệ")
    private String phone;
    @NotBlank(message = "Không bỏ trống email cửa hàng")
    @Email(message = "Định dạng email không chính xác")
    private String email;
    @NotBlank(message = "Không bỏ trống địa chỉ chi tiết cửa hàng")
    private String streetAddress;
    private String description;
    private Long idWard;
}
