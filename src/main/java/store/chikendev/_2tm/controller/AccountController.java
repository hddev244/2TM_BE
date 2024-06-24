package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.AccountRequest;
import store.chikendev._2tm.dto.request.LoginRequest;
import store.chikendev._2tm.dto.request.ChangePasswordRequest;
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
        AuthenticationResponse responce = authenticationService.auth(request, AuthenticationService.LOGIN_ROLE_USER);

        if (responce.isAuthenticated()) {
            Cookie cookie = new Cookie("accessToken", responce.getToken());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setSecure(true);
            // Thêm cookie vào phản hồi
            this.response.addCookie(cookie);
        }
        return new ApiResponse<AuthenticationResponse>(responce.isAuthenticated() ? 200 : 414, null, responce);
    }

    @PostMapping("admin/login")
    public ApiResponse<AuthenticationResponse> adminLogin(@RequestBody LoginRequest request) {
        AuthenticationResponse responce = authenticationService.auth(request, AuthenticationService.LOGIN_ROLE_ADMIN);

        if (responce.isAuthenticated()) {
            Cookie cookie = new Cookie("accessToken", responce.getToken());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setSecure(true);
            // Thêm cookie vào phản hồi
            this.response.addCookie(cookie);
        }
        return new ApiResponse<AuthenticationResponse>(responce.isAuthenticated() ? 200 : 414, null, responce);
    }

    @PostMapping("staff/login")
    public ApiResponse<AuthenticationResponse> staffLogin(@RequestBody LoginRequest request) {
        AuthenticationResponse responce = authenticationService.auth(request, AuthenticationService.LOGIN_ROLE_STAFF);

        if (responce.isAuthenticated()) {
            Cookie cookie = new Cookie("accessToken", responce.getToken());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setSecure(true);
            // Thêm cookie vào phản hồi
            this.response.addCookie(cookie);
        }
        return new ApiResponse<AuthenticationResponse>(responce.isAuthenticated() ? 200 : 414, null, responce);
    }

    @PostMapping("delevery/login")
    public ApiResponse<AuthenticationResponse> deleveryLogin(@RequestBody LoginRequest request) {
        AuthenticationResponse responce = authenticationService.auth(request,
                AuthenticationService.LOGIN_ROLE_DELIVERY);

        if (responce.isAuthenticated()) {
            Cookie cookie = new Cookie("accessToken", responce.getToken());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setSecure(true);
            // Thêm cookie vào phản hồi
            this.response.addCookie(cookie);
        }
        return new ApiResponse<AuthenticationResponse>(responce.isAuthenticated() ? 200 : 414, null, responce);
    }

    @GetMapping("logout")
    public ApiResponse<String> logout() {
        Cookie cookie = new Cookie("accessToken", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        this.response.addCookie(cookie);
        return new ApiResponse<String>(200, null, "Logout success");
    }

    @PostMapping("change-password")
    public ApiResponse<AccountResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        return new ApiResponse<AccountResponse>(200, null, accountService.changePassword(request));
    }

    @PostMapping(value = "updateImage", consumes = "multipart/form-data")
    public ApiResponse<String> updateImage(@RequestPart("id") String id,
            @RequestPart("image") MultipartFile image) {
        return new ApiResponse<String>(200, null, accountService.updateImage(id, image));
    }

}
