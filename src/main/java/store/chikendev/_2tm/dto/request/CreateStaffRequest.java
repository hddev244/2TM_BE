package store.chikendev._2tm.dto.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStaffRequest {
    @NotBlank(message = "không bỏ trống username")
    @Size(min = 5, message = "username phải từ 5 ký tự trở lên")
    private String username;
    @NotBlank(message = "Không bỏ trống tên của bạn")
    private String fullName;
    @NotBlank(message = "Không bỏ số điện thoại")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;
    @NotBlank(message = "Không bỏ trống email")
    @Email(message = "Định dạng email không chính xác")
    private String email;
    @NotNull(message = "Bạn cần phân quyền cho nhân viên")
    private List<String> roles;
    private Long idStore;

}
