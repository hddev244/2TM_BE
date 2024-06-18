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
public class LoginRequest {
    @NotBlank(message = "Không trống thông tin đăng nhập")
    private String username;
    @NotBlank(message = "không bỏ trống mật khẩu")
    @Size(min = 5, message = "Mật khẩu phải từ 5 ký tự trở lên")
    private String password;

}
