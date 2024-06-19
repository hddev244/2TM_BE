package store.chikendev._2tm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
// ma loi
public enum ErrorCode {

    // AUTH
    EMAIL_PHONE_EXISTED(411, "Email hoặc số điện thoại đã tồn tại", HttpStatus.BAD_REQUEST),
    USER_EXISTED(412, "Username đã tồn tại", HttpStatus.BAD_REQUEST),
    ACCOUNT_BLOCKED(413, "Tài khoản của bạn đã bị khóa do vi phạm chính sách", HttpStatus.BAD_REQUEST),
    ACCOUNT_NO_VERIFIED(414, "Tài khoản của bạn chưa được xác thực", HttpStatus.BAD_REQUEST),
    LOGIN_FAIL(415, "Đăng nhập không chính xác", HttpStatus.BAD_REQUEST),
    
    STATE_NOT_FOUND(431, "Không tìm thấy trạng thái", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(432, "Không tìm thấy danh mục", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(433, "Thông tin đăng nhập không chính xác", HttpStatus.BAD_REQUEST),
    
    // File
    ROLE_NOT_FOUND(420, "Không tìm thấy vai trò", HttpStatus.BAD_REQUEST),
    WARD_NOT_FOUND(421, "Không tìm thấy địa chỉ", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_FOUND(422, "Không tìm thấy hình ảnh", HttpStatus.BAD_REQUEST),
    STORE_NOT_FOUND(423, "Không tìm thấy cửa hàng", HttpStatus.BAD_REQUEST),
    EXCEL_IMPORT_ERROR(425, "Nhập file excel lỗi", HttpStatus.BAD_REQUEST),
    EXCEL_EXPORT_ERROR(426, "Xuất file excel lỗi", HttpStatus.BAD_REQUEST),

    // OTP
    OTP_ERROR(441, "Không thể gửi OTP", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(442, "Mã OTP đã hết hạn", HttpStatus.BAD_REQUEST),
    OTP_INFO_INVALID(443, "Thông tin xác thực không tồn tại, vui lòng kiểm tra thông tin đăng kí hoặc tạo tài khoản!", HttpStatus.BAD_REQUEST),
    OTP_INVALID(444, "Mã OTP không đúng", HttpStatus.BAD_REQUEST),
    OTP_ACCOUNT_VERIFIED(445, "Tài khoản đã được xác thực", HttpStatus.BAD_REQUEST),
    OTP_REQUEST_NOT_FOUND(446, "Yêu cầu xác thực không tồn tại", HttpStatus.BAD_REQUEST),

    FILE_UPLOAD_ERROR(428, "Lỗi khi tải file lên", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(500, "Lỗi máy chủ", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(401, "Thông tin xác nhận không hợp lệ", HttpStatus.UNAUTHORIZED),
    ;

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
