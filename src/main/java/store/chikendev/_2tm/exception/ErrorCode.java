package store.chikendev._2tm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
// ma loi
public enum ErrorCode {
    EMAIL_PHONE_EXISTED(400, "Email hoặc số điện thoại đã tồn tại", HttpStatus.BAD_REQUEST),
    USER_EXISTED(400, "Username đã tồn tại", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(400, "Không tìm thấy tài khoản", HttpStatus.BAD_REQUEST),
    EXCEL_IMPORT_ERROR(400, "Nhập file excel lỗi", HttpStatus.BAD_REQUEST),
    EXCEL_EXPORT_ERROR(400, "Xuất file excel lỗi", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(400, "Không tìm thấy vai trò", HttpStatus.BAD_REQUEST),
    WARD_NOT_FOUND(400, "Không tìm thấy địa chỉ", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_FOUND(400, "Không tìm thấy hình ảnh", HttpStatus.BAD_REQUEST),
    STORE_NOT_FOUND(400, "Không tìm thấy cửa hàng", HttpStatus.BAD_REQUEST),
    OTP_ERROR(400, "Không thể gửi OTP", HttpStatus.BAD_REQUEST),
    LOGIN_FAIL(400, "Đăng nhập không chính xác", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(500, "Lỗi máy chủ", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(401, "Thông tin xác nhận không hợp lệ", HttpStatus.UNAUTHORIZED),
    FILE_UPLOAD_ERROR(400, "Lỗi khi tải file lên", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
