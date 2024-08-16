package store.chikendev._2tm.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PreAuthorize("hasAnyRole('ROLE_KH')")
    @PostMapping("/require")
    public ApiResponse<OwnerPermissionResponse> addOwnerPermission(@RequestBody @Valid OwnerPermissionRequest request) {
        OwnerPermissionResponse response = ownerPermissionService.addOwnerPermission(request);

        if (response == null) {
            return new ApiResponse<OwnerPermissionResponse>(200,
                    List.of("Tạo thành công yêu cầu"), null);
        }

        return new ApiResponse<OwnerPermissionResponse>(777, null, response);
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

    @PreAuthorize("hasRole('ROLE_QTV')")
    @PostMapping("/reject/{id}")
    public ApiResponse<String> cancelOwnerPermission(@PathVariable("id") Long id) {
        ownerPermissionService.cancelOwnerPermission(id);
        return new ApiResponse<String>(200, null, "Yêu cầu đã bị từ chối");
    }

    @PreAuthorize("hasRole('ROLE_QTV')")
    @PostMapping("/confirm/{id}")
    public ApiResponse<String> confirmOwnerPermission(@PathVariable("id") Long id) {
        ownerPermissionService.confirmOwnerPermission(id);
        return new ApiResponse<String>(200, null, "Xác nhận thành công");
    }
}
