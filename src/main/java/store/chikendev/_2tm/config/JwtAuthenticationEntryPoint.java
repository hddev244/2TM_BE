package store.chikendev._2tm.config;

import java.io.IOException;
import java.util.Collections;

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

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
            String token = authenticationService.getTokenFromRequest(request);

            if (token != null) { 
               try {
                   authenticationService.verifyToken(token, false);
               } catch (Exception e) {
                    AppException appException = (AppException) e;
                   ErrorCode errorCode = appException.getErrorCode();
                   response.setStatus(errorCode.getStatusCode().value());
                   response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                   ApiResponse<?> apiResponse = ApiResponse.builder()
                           .code(errorCode.getCode())
                           .message(Collections.singletonList(appException.getMessage()))
                           .build();
                   ObjectMapper objectMapper = new ObjectMapper();
                   response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
                   response.flushBuffer();
                   return;
               }
            } 
        
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
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
