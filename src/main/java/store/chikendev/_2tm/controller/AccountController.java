package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.AccountRequest;
import store.chikendev._2tm.dto.request.ChangePasswordRequest;
import store.chikendev._2tm.dto.request.LoginRequest;

import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.AuthenticationResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.entity.Account;
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

    @GetMapping
    public ApiResponse<AccountResponse> getAccount() {
        return new ApiResponse<AccountResponse>(200, null, accountService.getAccountByToken());
    }

    @PostMapping("register")
    public ApiResponse<AccountResponse> register(@RequestBody @Valid AccountRequest request) {
        AccountResponse response = accountService.register(request);
        otpService.sendOtp(response.getEmail());
        return new ApiResponse<AccountResponse>(200, null, response);
    }

    @PostMapping("login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse responce = authenticationService.auth(request, AuthenticationService.LOGIN_ROLE_USER);

        return new ApiResponse<AuthenticationResponse>(responce.isAuthenticated() ? 200 : 414, null, responce);
    }

    @PostMapping("admin/login")
    public ApiResponse<AuthenticationResponse> adminLogin(@RequestBody LoginRequest request) {
        AuthenticationResponse responce = authenticationService.auth(request, AuthenticationService.LOGIN_ROLE_ADMIN);

        return new ApiResponse<AuthenticationResponse>(responce.isAuthenticated() ? 200 : 414, null, responce);
    }

    @PostMapping("staff/login")
    public ApiResponse<AuthenticationResponse> staffLogin(@RequestBody LoginRequest request) {
        AuthenticationResponse responce = authenticationService.auth(request, AuthenticationService.LOGIN_ROLE_STAFF);

        return new ApiResponse<AuthenticationResponse>(responce.isAuthenticated() ? 200 : 414, null, responce);
    }

    @PostMapping("delevery/login")
    public ApiResponse<AuthenticationResponse> deleveryLogin(@RequestBody LoginRequest request) {
        AuthenticationResponse responce = authenticationService.auth(request,
                AuthenticationService.LOGIN_ROLE_DELIVERY);

        return new ApiResponse<AuthenticationResponse>(responce.isAuthenticated() ? 200 : 414, null, responce);
    }

    @GetMapping("logout")
    public ApiResponse<String> logout() {
        return new ApiResponse<String>(200, null, "Logout success");
    }

    @PostMapping("change-password")
    public ApiResponse<String> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        return new ApiResponse<String>(200, null, accountService.changePassword(request) ? "Đổi mật khẩu thành công"
                : "Đổi mật khẩu thất bại");
    }

    @PostMapping(value = "updateImage", consumes = "multipart/form-data")
    public ApiResponse<ResponseDocumentDto> updateImage(@RequestPart("id") String id,
            @RequestPart("image") MultipartFile image) {
        return new ApiResponse<ResponseDocumentDto>(200, null, accountService.updateImage(id, image));
    }

    @PostMapping(value = "changeAvatar", consumes = "multipart/form-data")
    public ApiResponse<ResponseDocumentDto> updateImagePersonal(
            @RequestPart("image") MultipartFile image) {
        return new ApiResponse<ResponseDocumentDto>(200, null, accountService.changeAvatar(image));
    }

    @PutMapping("/{id}")
    public ApiResponse<Account> updateAccount(@PathVariable("id") String id,
            @RequestBody AccountRequest updateAccountRequest) {
        Account updatedAccount = accountService.updateAccountById(id, updateAccountRequest);
        return new ApiResponse<Account>(200, null, updatedAccount);
    }

    @PreAuthorize("hasRole('ROLE_QTV')")
    @GetMapping("/{id}")
    public ApiResponse<AccountResponse> getAccountById(@PathVariable(name = "id") String id) {
        AccountResponse accountResponse = accountService.getStaffById(id);
        return new ApiResponse<AccountResponse>(200, null, accountResponse);
    }

}
