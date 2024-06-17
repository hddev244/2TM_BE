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

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private HttpServletResponse response;

    @PostMapping("register")
    public ApiResponse<AccountResponse> register(@RequestBody @Valid AccountRequest request) {
        return new ApiResponse<AccountResponse>(200, null, accountService.register(request));
    }

    @PostMapping("login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse responce = authenticationService.auth(request);
        Cookie cookie = new Cookie(responce.getIdUser(), responce.getToken());
        cookie.setPath("/accessToken");
        cookie.setMaxAge(24 * 60 * 60);

        // Thêm cookie vào phản hồi
        this.response.addCookie(cookie);
        return new ApiResponse<AuthenticationResponse>(200, null, responce);
    }

}
