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
        ADDRESS_NOT_FOUND(450, "Không tìm thấy địa chỉ", HttpStatus.BAD_REQUEST),
        EMAIL_PHONE_EXISTED(411, "Email hoặc số điện thoại đã tồn tại", HttpStatus.BAD_REQUEST),
        USER_EXISTED(412, "Username đã tồn tại", HttpStatus.BAD_REQUEST),
        ACCOUNT_BLOCKED(413, "Tài khoản của bạn đã bị khóa do vi phạm chính sách", HttpStatus.BAD_REQUEST),
        ACCOUNT_NO_VERIFIED(414, "Tài khoản của bạn chưa được xác thực", HttpStatus.BAD_REQUEST),
        LOGIN_FAIL(415, "Thông tin đăng nhập không chính xác", HttpStatus.BAD_REQUEST),
        PASSWORD_NOT_MATCH(416, "Nhập lại mật khẩu không đúng", HttpStatus.BAD_REQUEST),
        PASSWORD_NOT_FOUND(417, "Mật khẩu cũ bạn nhập sai", HttpStatus.BAD_REQUEST),
        LOGIN_ROLE_REQUIRED(403, "Tài khoản không có quyền truy cập!", HttpStatus.FORBIDDEN),
        NO_MANAGEMENT_RIGHTS(418, "Đơn hàng này hiện không thuộc quản lý của bạn", HttpStatus.BAD_REQUEST),
        USER_NAME_EXISTED(419, "Tên người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
        OWNER_PERMISSION_NOT_FOUND(424, "Yêu cầu thất bại", HttpStatus.BAD_REQUEST),
        OWNER_PERMISSION_EXIST(425, "Yêu cầu đã tồn tại", HttpStatus.BAD_REQUEST),
        DATA_ERROR(000, "Lỗi cơ sở dữ liệu", HttpStatus.BAD_REQUEST),

        STATE_NOT_FOUND(431, "Không tìm thấy trạng thái", HttpStatus.BAD_REQUEST),
        CATEGORY_NOT_FOUND(432, "Không tìm thấy danh mục", HttpStatus.BAD_REQUEST),
        USER_NOT_FOUND(433, "Thông tin đăng nhập không chính xác", HttpStatus.BAD_REQUEST),
        ORDER_NOT_FOUND(434, "Tông tin order không chính xác", HttpStatus.BAD_REQUEST),
        PRODUCT_NOT_FOUND(435, "Không tìm thấy sản phẩm", HttpStatus.BAD_REQUEST),
        ATTRIBUTE_NOT_FOUND(436, "Không tìm thấy thuộc tính", HttpStatus.BAD_REQUEST),
        QUANTITY_ERROR(437, "Số lượng vượt quá số hàng tồn kho", HttpStatus.BAD_REQUEST),
        DELIVERY_PERSON_NOT_FOUND(438, "Khu vực của bạn không hỗ trợ lấy hàng", HttpStatus.BAD_REQUEST),
        PAYMENT_ERROR(439, "Thanh toán không thành công", HttpStatus.BAD_REQUEST),
        CONSIGNMENT_ORDER_NOT_FOUND(440, "Không tìm thấy vận đơn", HttpStatus.BAD_REQUEST),
        STATE_ERROR(461, "Trạng thái không phù hợp để thay đổi", HttpStatus.BAD_REQUEST),
        PAYMENT_METHOD_NOT_FOUND(460, "không tìm thấy phương thức thanh toán", HttpStatus.BAD_REQUEST),
        CART_EMPTY(459, "không tìm thấy sản phẩm trong giỏ hàng của bạn", HttpStatus.BAD_REQUEST),
        PRODUCT_NOT_ENOUGH(458, "Số lượng sản phẩm tồn kho không đủ", HttpStatus.BAD_REQUEST),
        PAYMENT_RECORD_NOT_FOUND(457, "Không tìm thấy hóa đơn thanh toán", HttpStatus.BAD_REQUEST),
        CART_ITEM_NOT_MATCHING_ACCOUNT(456, "Sản phẩm trong giỏ hàng không phù hợp với tài khoản",
                        HttpStatus.BAD_REQUEST),
        VOUCHER_NOT_FOUND(457, "Không tìm thấy voucher", HttpStatus.BAD_REQUEST),
        PAYMENT_DISBURSEMENT_NOT_FOUND(458, "Không tìm thấy thông tin thanh toán", HttpStatus.BAD_REQUEST),     

        ORDER_NOT_CONFIRMED(455, "Đơn hàng chưa xác nhận", HttpStatus.BAD_REQUEST),
        DELIVERY_PERSON_EMPTY(454, "Không tìm thấy nhân viên giao hàng", HttpStatus.BAD_REQUEST),
        STATE_ORDER_NOT_FOUND(453, "Không tìm thấy trạng thái order", HttpStatus.BAD_REQUEST),
        ROLE_ERROR(452, "Vai trò không phù hợp để thực hiện chức năng", HttpStatus.BAD_REQUEST),
        BILL_OF_LADING_NOT_FOUND(462, "Không tìm thấy vận đơn", HttpStatus.BAD_REQUEST),
        INVALID_STATUS_CHANGE(463, "Trạng thái thay đổi không phù hợp", HttpStatus.BAD_REQUEST),

        INVALID_COMMISSION_RATE(464, "Tỷ lệ hoa hồng phải dưới 10%", HttpStatus.BAD_REQUEST),
        SHIPPING_COST_NOT_FOUND(465, "Không tìm thấy chi phí vận chuyển", HttpStatus.BAD_REQUEST),
        INVAL_DATETIME_INPUT(466, "Định dạng ngày tháng sai", HttpStatus.BAD_REQUEST),

        // payment
        PAYMENT_ERROR_05(470, "Tài khoản của bạn không đủ số dư để thực hiện giao dịch", HttpStatus.BAD_REQUEST),
        PAYMENT_ERROR_06(471, "Bạn nhập sai mã OTP", HttpStatus.BAD_REQUEST),
        PAYMENT_ERROR_09(472, "Thẻ/Tài khoản của bạn chưa đăng ký dịch vụ InternetBanking tại ngân hàng.",
                        HttpStatus.BAD_REQUEST),
        PAYMENT_ERROR_10(473, "bạn xác thực thông tin thẻ/tài khoản không đúng quá 3 lần", HttpStatus.BAD_REQUEST),
        PAYMENT_ERROR_11(474, "Đã hết hạn chờ thanh toán", HttpStatus.BAD_REQUEST),
        PAYMENT_ERROR_12(475, "Thẻ/Tài khoản của bạn bị khóa", HttpStatus.BAD_REQUEST),
        PAYMENT_ERROR_24(476, "Bạn đã hủy giao dịch", HttpStatus.BAD_REQUEST),
        PAYMENT_ERROR_79(477, "Bạn nhập sai mật khẩu thanh toán quá số lần quy định", HttpStatus.BAD_REQUEST),
        PAYMENT_ERROR_65(478, "Tài khoản của bạn đã vượt quá hạn mức giao dịch trong ngày", HttpStatus.BAD_REQUEST),
        PAYMENT_ERROR_75(479, "Ngân hàng thanh toán đang bảo trì", HttpStatus.BAD_REQUEST),
        PAYMENT_ERROR_99(480, "Thanh toán bị lỗi không xác định", HttpStatus.BAD_REQUEST),

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
        OTP_INFO_INVALID(443,
                        "Thông tin xác thực không tồn tại, vui lòng kiểm tra thông tin đăng kí hoặc tạo tài khoản!",
                        HttpStatus.BAD_REQUEST),
        OTP_INVALID(444, "Mã OTP không đúng", HttpStatus.BAD_REQUEST),
        OTP_ACCOUNT_VERIFIED(445, "Tài khoản đã được xác thực", HttpStatus.BAD_REQUEST),
        OTP_REQUEST_NOT_FOUND(446, "Yêu cầu xác thực không tồn tại", HttpStatus.BAD_REQUEST),

        FILE_UPLOAD_ERROR(428, "Lỗi khi tải file lên", HttpStatus.BAD_REQUEST),
        INTERNAL_SERVER_ERROR(500, "Lỗi máy chủ", HttpStatus.INTERNAL_SERVER_ERROR),
        UNAUTHORIZED(401, "Thông tin xác nhận không hợp lệ", HttpStatus.UNAUTHORIZED),
        TOKEN_EXPIRED(402, "Phiên đăng nhập đã hết hạn", HttpStatus.UNAUTHORIZED),
        FILE_NOT_FOUND(429, "Bạn không thể bỏ trống ảnh trong mục này", HttpStatus.BAD_REQUEST),
        ORDER_ERROR(447, "Order xảy ra lỗi, vui lòng thử lại sau!", HttpStatus.BAD_REQUEST),
        INVALID_QUANTITY(448, "Số lượng không hợp lệ", HttpStatus.BAD_REQUEST),
        CART_DELETED(449, "Sản phẩm đã được xóa khỏi giỏ hàng", HttpStatus.BAD_REQUEST),
        CART_QTY_BIGGER_THAN_PRODUCT(450, "Số lượng sản phẩm trong giỏ hàng lớn hơn số lượng sản phẩm tồn kho",
                        HttpStatus.BAD_REQUEST),
        TOKEN_INVALID(451, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),

        ;

        private int code;
        private String message;
        private HttpStatusCode statusCode;
}
