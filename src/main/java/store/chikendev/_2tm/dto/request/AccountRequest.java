package store.chikendev._2tm.dto.request;

import jakarta.validation.constraints.Email;
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
public class AccountRequest {
    @NotBlank(message = "không bỏ trống mật khẩu")
    @Size(min = 5, message = "Mật khẩu phải từ 5 ký tự trở lên")
    private String password;
    @NotBlank(message = "Không bỏ trống tên của bạn")
    private String fullName;
    @NotBlank(message = "Không bỏ trống email")
    @Email(message = "Định dạng email không chính xác")
    private String email;

    
}
