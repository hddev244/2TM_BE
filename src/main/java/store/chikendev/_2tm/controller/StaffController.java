package store.chikendev._2tm.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.CreateStaffRequest;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.CreateStaffResponse;
import store.chikendev._2tm.service.AccountService;

@RestController
@RequestMapping("/api/account")
public class StaffController {
    @Autowired
    private AccountService accountService;

    @PostMapping("create-staff")
    public ApiResponse<CreateStaffResponse> createStaff(@RequestBody @Valid CreateStaffRequest request) {
        CreateStaffResponse response = accountService.createStaff(request);
        return new ApiResponse<CreateStaffResponse>(200, null, response);
    }

    @GetMapping("Staff/{page}")
    public ApiResponse<Page<CreateStaffResponse>> getStaff(@PathVariable("page") Optional<Integer> pageNo) {
        return new ApiResponse<Page<CreateStaffResponse>>(200, null, accountService.getAllStaff(pageNo));
    }

}
