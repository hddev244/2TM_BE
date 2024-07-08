package store.chikendev._2tm.config;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.service.AuthenticationService;

// bắt lỗi khi sài phương thức @PreAuthorize("hasRole('ADMIN')") để phân quyền
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private AuthenticationService authenticationService;

    // In AuthenticationService.java
    public boolean isPublicResource(HttpServletRequest request) {
        // Danh sách các API không cần xác thực token
        // Nếu request URI nằm trong danh sách này thì không cần xác thực token
        List<String> publicResources = List.of(
                "/api/account/login",
                "/api/account/register",
                "/api/account/refresh-token",
                "/api/account/forgot-password",
                "/api/account/reset-password",
                "/api/account/verify-email",
                "/api/account/verify-phone",
                "/api/account/verify-otp",
                "/api/account/resend-otp"
        );
        return publicResources.contains(request.getRequestURI());
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        if (isPublicResource(request)) {
            request.getRequestDispatcher(request.getRequestURI()).forward(request, response);
            return;
        }
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        String token = authenticationService.getTokenFromRequest(request);

        if (token != null) {
            try {
                authenticationService.verifyToken(token, false);
            } catch (Exception e) {
                AppException appException = (AppException) e;
                errorCode = appException.getErrorCode();
            } finally {
                response.setStatus(errorCode.getStatusCode().value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                ApiResponse<?> apiResponse = ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(Collections.singletonList("Thông tin xác nhận không chính xác"))
                        .build();
                ObjectMapper objectMapper = new ObjectMapper();
                response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
                response.flushBuffer();
            }
        }
    }
}
