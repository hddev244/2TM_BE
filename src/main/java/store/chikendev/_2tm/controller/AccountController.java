package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.AccountRequest;
import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.service.AccountService;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("register")
    public ApiResponse<AccountResponse> register(@RequestBody @Valid AccountRequest request) {
        return new ApiResponse<AccountResponse>(200, null, accountService.register(request));
    }
}
