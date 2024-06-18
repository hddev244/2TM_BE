package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.AccountRequest;
import store.chikendev._2tm.dto.request.LoginRequest;
import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.AuthenticationResponse;
import store.chikendev._2tm.service.AccountService;
import store.chikendev._2tm.service.AuthenticationService;
import store.chikendev._2tm.service.OtpService;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private HttpServletResponse response;

    @PostMapping("register")
    public ApiResponse<AccountResponse> register(@RequestBody @Valid AccountRequest request) {
        AccountResponse response = accountService.register(request);
        otpService.sendOtp(response.getEmail());
        return new ApiResponse<AccountResponse>(200, null, response);
    }

    @PostMapping("login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse responce = authenticationService.auth(request);

        if (responce.isAuthenticated()){
            Cookie cookie = new Cookie("accessToken", responce.getToken());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(24 * 60 * 60);
            // Thêm cookie vào phản hồi
            this.response.addCookie(cookie);
        } else {
            Cookie cookie = new Cookie("accessToken", "");
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            this.response.addCookie(cookie);
        }
        return new ApiResponse<AuthenticationResponse>(200, null, responce);
    }

}
