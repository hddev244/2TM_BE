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
public class ChangePasswordRequest {
    @NotBlank(message = "Không bỏ trống ô mật khẩu cũ")
    private String passwordOld;
    @NotBlank(message = "Không bỏ trống ô mật khẩu mới")
    @Size(min = 8, message = "Mật khẩu từ 8 ký tự trở lên")
    private String passwordNew;
    @NotBlank(message = "Không bỏ trống ô nhập lại mật khẩu")
    @Size(min = 8, message = "Nhập lại mật khẩu từ 8 ký tự trở lên")
    private String passwordConfirm;
}
