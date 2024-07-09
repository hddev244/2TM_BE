package store.chikendev._2tm.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.CreateStaffRequest;
import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.CreateStaffResponse;
import store.chikendev._2tm.service.AccountService;

@RestController
@RequestMapping("/api/account")
public class StaffController {
    @Autowired
    private AccountService accountService;

    @PreAuthorize("hasAnyRole('ROLE_QTV')")
    @PostMapping("create-staff")
    public ApiResponse<CreateStaffResponse> createStaff(@RequestBody @Valid CreateStaffRequest request) {
        CreateStaffResponse response = accountService.createStaff(request);
        return new ApiResponse<CreateStaffResponse>(200, null, response);
    }

    @PreAuthorize("hasAnyRole('ROLE_QTV')")
    @GetMapping("staff")
    public ApiResponse<Page<CreateStaffResponse>> getStaff(@RequestParam(name = "pageNo" , required = false) Optional<Integer> pageNo) {
       
        return new ApiResponse<Page<CreateStaffResponse>>(200, null, accountService.getAllStaff(pageNo));
    }

    @PreAuthorize("hasAnyRole('ROLE_QTV', 'ROLE_NVCH', 'ROLE_NVGH', 'ROLE_QLCH')")
    @PutMapping("staff/{id}")
    public ApiResponse<CreateStaffResponse> updateStaff(@PathVariable("id") String id,
            @RequestBody @Valid CreateStaffRequest request) {
        return new ApiResponse<CreateStaffResponse>(200, null, accountService.updateStaff(id, request));
    }

    @PreAuthorize("hasAnyRole('ROLE_QTV')")
    @PutMapping("/lock-staff/{id}")
    public ApiResponse<AccountResponse> lockAccount(@PathVariable("id") String id) {
        AccountResponse updatedAccount = accountService.lockAccount(id);
        return new ApiResponse<AccountResponse>(200, null, updatedAccount);
    }

    @PreAuthorize("hasAnyRole('ROLE_QTV')")
    @PostMapping("update-role")
    public ApiResponse<String> updateRole(@RequestParam("idAccount") String idAccount,
            @RequestParam("idRole") String idRole) {
        String updatedAccount = accountService.updateRoleNV(idAccount, idRole);
        return new ApiResponse<String>(200, null, updatedAccount);
    }

}
