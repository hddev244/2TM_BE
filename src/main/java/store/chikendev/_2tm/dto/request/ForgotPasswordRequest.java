package store.chikendev._2tm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordRequest {
    @NotBlank(message = "không bỏ trống token")
    private String token;
    @NotBlank(message = "không bỏ trống mật khẩu mới")
    @Size(min = 5, message = "Mật khẩu mới phải từ 5 ký tự trở lên")
    private String newPassword;
    @NotBlank(message = "không bỏ trống nhập lại mật khẩu mới")
    @Size(min = 5, message = "Nhập lại mật khẩu mới phải từ 5 ký tự trở lên")
    private String reNewPassword;

}
