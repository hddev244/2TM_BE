package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.dto.request.AccountRequest;
import store.chikendev._2tm.dto.request.LoginRequest;
import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.AuthenticationResponse;
import store.chikendev._2tm.service.AccountService;
import store.chikendev._2tm.service.AuthenticationService;
import store.chikendev._2tm.service.OtpService;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AccountService accountService;

    // @Autowired
    // private OtpService otpService;

    @GetMapping
    public String test() {
        return "Hello, World!";
    }

    @PostMapping("auth")
    public ApiResponse<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        return new ApiResponse<AuthenticationResponse>(200, null, authenticationService.auth(request));
    }

    @PostMapping("create")
    public ApiResponse<AccountResponse> create(@RequestBody AccountRequest request) {
        return new ApiResponse<AccountResponse>(200, null, accountService.register(request));
    }

    // @PostMapping("otp")
    // public ApiResponse<String> otp(@RequestParam String number) {
    // return new ApiResponse<>(200, null, otpService.sendOtp(number));

    // }

}
