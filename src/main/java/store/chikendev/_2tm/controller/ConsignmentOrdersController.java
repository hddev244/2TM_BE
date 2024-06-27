package store.chikendev._2tm.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.ConsignmentOrdersRequest;
import store.chikendev._2tm.service.ConsignmentOrdersService;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.ConsignmentOrdersResponse;

@RestController
@RequestMapping("/api/consignment")
public class ConsignmentOrdersController {

    @Autowired
    private ConsignmentOrdersService consignmentOrdersService;

    @PreAuthorize("hasRole('ROLE_CH')")
    @PostMapping(value = "create", consumes = "multipart/form-data")
    public ApiResponse<String> staffCreate(@RequestPart("consignmentOrders") @Valid ConsignmentOrdersRequest request,
            @RequestPart("images") MultipartFile[] images) {
        return new ApiResponse<String>(200, null,
                consignmentOrdersService.createConsignmentOrders(request, images));
    }

    @PreAuthorize("hasAnyRole('ROLE_QTV', 'ROLE_QLCH', 'ROLE_CH', 'ROLE_NVCH','ROLE_NVGH')")
    @GetMapping
    public ApiResponse<Page<ConsignmentOrdersResponse>> getByState(
            @RequestParam(required = false, name = "size") Optional<Integer> size,
            @RequestParam(required = false, name = "page") Optional<Integer> page,
            @RequestParam(required = false, name = "state") Long state) {
        return new ApiResponse<Page<ConsignmentOrdersResponse>>(200, null,
                consignmentOrdersService.getByState(size.orElse(10), page.orElse(0), state));
    }
}
