package store.chikendev._2tm.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ApiResponse<Page<ConsignmentOrdersResponse>> getByStateOrAll(
            @RequestParam(required = false, name = "size") Optional<Integer> size,
            @RequestParam(required = false, name = "page") Optional<Integer> page,
            @RequestParam(required = false, name = "state") Long state) {
        return new ApiResponse<Page<ConsignmentOrdersResponse>>(200, null,
                consignmentOrdersService.getByStateOrAll(size.orElse(10), page.orElse(0), state));
    }

    @PreAuthorize("hasAnyRole('ROLE_CH')")
    @GetMapping("owner-find-by-state")
    public ApiResponse<Page<ConsignmentOrdersResponse>> getByStateOrAllWithOwner(
            @RequestParam(required = false, name = "size") Optional<Integer> size,
            @RequestParam(required = false, name = "page") Optional<Integer> page,
            @RequestParam(required = false, name = "state") Long state) {
        return new ApiResponse<Page<ConsignmentOrdersResponse>>(200, null,
                consignmentOrdersService.getByStateOrAllWithOwner(size.orElse(10), page.orElse(0), state));
    }

    @PreAuthorize("hasRole('ROLE_NVGH')")
    @PutMapping(value = "update-status", consumes = "multipart/form-data")
    public ApiResponse<String> updateStatus(@RequestPart("consignmentOrderId") Long id,
            @RequestPart("stateId") Long stateId,
            @RequestPart(required = false, name = "image") MultipartFile image) {
        return new ApiResponse<String>(200, null, consignmentOrdersService.updateStatus(stateId, id, image));
    }

    @PreAuthorize("hasAnyRole('ROLE_QLCH','ROLE_NVCH')")
    @GetMapping("success/{consignmentOrderId}")
    public ApiResponse<String> success(@PathVariable("consignmentOrderId") Long id) {
        return new ApiResponse<String>(200, null, consignmentOrdersService.successConsignmentOrders(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_QLCH','ROLE_NVCH')")
    @GetMapping("confirm/{consignmentOrderId}")
    public ApiResponse<String> confirm(@PathVariable("consignmentOrderId") Long id) {
        return new ApiResponse<String>(200, null, consignmentOrdersService.confirmOrder(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_QLCH','ROLE_NVCH')")
    @GetMapping("refuse/{consignmentOrderId}")
    public ApiResponse<String> refuse(@PathVariable("consignmentOrderId") Long id) {
        return new ApiResponse<String>(200, null, consignmentOrdersService.refuseOrder(id));
    }

    @GetMapping("/find-by-id/{id}")
    public ApiResponse<ConsignmentOrdersResponse> getConsignmentOrderById(@PathVariable("id") Long id) {
        ConsignmentOrdersResponse response = consignmentOrdersService.getConsignmentOrderById(id);
        return new ApiResponse<ConsignmentOrdersResponse>(200, null, response);
    }

    @PreAuthorize("hasAnyRole('ROLE_CH')")
    @PostMapping("/Cannel_consignmentOrder/{consignmentOrderId}")
    public ApiResponse<String> cancelConsignmentOrder(@PathVariable("consignmentOrderId") Long id) {
        consignmentOrdersService.cancelConsignmentOrder(id);
        return new ApiResponse<>(200, null, "Yêu cầu ký gửi đã được hủy thành công.");
    }

    @PreAuthorize("hasRole('ROLE_CH')")
    @GetMapping("/details")
    public ApiResponse<ConsignmentOrdersResponse> getConsignmentOrders(@RequestParam(name = "id") Long id) {
        ConsignmentOrdersResponse response = consignmentOrdersService.getConsignmentOrders(id);
        return new ApiResponse<ConsignmentOrdersResponse>(200, null, response);
    }

}
