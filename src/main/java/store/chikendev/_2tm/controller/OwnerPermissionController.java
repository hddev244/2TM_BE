package store.chikendev._2tm.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import store.chikendev._2tm.dto.request.OwnerPermissionRequest;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.OwnerPermissionResponse;
import store.chikendev._2tm.service.OwnerPermissionService;

@RestController
@RequestMapping("/api/ownerPermission")
public class OwnerPermissionController {
    @Autowired
    private OwnerPermissionService ownerPermissionService;

    @PostMapping("/addOwnerPermission")
    public ApiResponse<OwnerPermissionResponse> addOwnerPermission(@RequestBody OwnerPermissionRequest request) {
        System.out.println(request.getBankAccountNumber());
        OwnerPermissionResponse response = ownerPermissionService.addOwnerPermission(request);
        return new ApiResponse<OwnerPermissionResponse>(200, null, response);
    }

    @PreAuthorize("hasRole('ROLE_QTV')")
    @GetMapping
    public ApiResponse<Page<OwnerPermissionResponse>> getAllOwnerPermission(
            @RequestParam(required = false, name = "size") Optional<Integer> size,
            @RequestParam(required = false, name = "page") Optional<Integer> page,
            @RequestParam(required = false, name = "state") Long state) {
        Page<OwnerPermissionResponse> response = ownerPermissionService.getOwnerPer(size.orElse(10), page.orElse(0),
                state);
        return new ApiResponse<Page<OwnerPermissionResponse>>(200, null, response);
    }

    // @PreAuthorize("hasRole('ROLE_QTV')")
    // @PutMapping("/update/status/{id}")
    // public ApiResponse<String> updateStatus(@PathVariable("id") Long id,
    // @RequestBody UpdateStateOwwnerPermissionRequest request) {
    // String status = ownerPermissionService.updateState(id, request);
    // return new ApiResponse<String>(200, null, status);
    // }

}
