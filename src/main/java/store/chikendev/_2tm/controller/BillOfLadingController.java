package store.chikendev._2tm.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.BillOfLadingResponse;
import store.chikendev._2tm.service.BillOfLadingService;

@RestController
@RequestMapping("/api/billOfLading")
public class BillOfLadingController {

    @Autowired
    private BillOfLadingService billOfLadingService;

    @PreAuthorize("hasAnyRole('ROLE_NVGH', 'ROLE_NVCH')")
    @GetMapping("/{deliveryPerson}")
    public ApiResponse<List<BillOfLadingResponse>> getShipList(@PathVariable("deliveryPerson") String deliveryPerson) {
        return new ApiResponse<List<BillOfLadingResponse>>(200, null,
                billOfLadingService.getBillOfLadingByDeliveryPersonId(deliveryPerson));
    }

    @PreAuthorize("hasAnyRole('ROLE_NVGH')") 
    @PutMapping("/accept/{billOfLadingId}")
    public ApiResponse<String> aceptBillOfLading(
            @PathVariable("billOfLadingId") Long billOfLadingId) {
                System.out.println("billOfLadingId: " + billOfLadingId);
        billOfLadingService.acceptBillOfLading(billOfLadingId);
        return new ApiResponse<String>(200, null, "Lấy hàng thành công");
    }

    @PreAuthorize("hasAnyRole('ROLE_NVGH')")
    @GetMapping("/shipList")
    public ApiResponse<Page<BillOfLadingResponse>> getByDeliveryPersonIdAndStateId(
            @RequestParam(required = false, name = "size") Optional<Integer> size,
            @RequestParam(required = false, name = "page") Optional<Integer> page,
            @RequestParam(required = false, name = "stateId") Long stateId) {
        Page<BillOfLadingResponse> response = billOfLadingService.getByDeliveryPersonIdAndStateId(size.orElse(10),
                page.orElse(0), stateId);
        return new ApiResponse<Page<BillOfLadingResponse>>(200, null, response);
    }

    // xác nhận và tạo bill
    @PreAuthorize("hasAnyRole('ROLE_QLCH', 'ROLE_NVCH')")
    @GetMapping("/confirm-order")
    public ApiResponse<String> confirmOrder(@RequestParam(name = "orderId") Long orderId) {
        return new ApiResponse<String>(200, null, billOfLadingService.confirmOder(orderId));
    }

    @PreAuthorize("hasAnyRole('ROLE_KH')")
    @PostMapping("/Cancelled/{orderId}")
    public ApiResponse<String> cancelOrder(@PathVariable("orderId") Long orderId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        billOfLadingService.cancelOrder(orderId, email);
        return new ApiResponse<>(200, null, "Hủy đơn hàng thành công");
    }

   @PreAuthorize("hasRole('ROLE_NVGH')")
    @PutMapping(value = "delivery-person/complete", consumes = "multipart/form-data")
    public ApiResponse<String> completeBill(@RequestPart("id") Long id,
            @RequestPart(required = false, name = "image") MultipartFile image) {
                String res = billOfLadingService.completeBill(id, image);
        return new ApiResponse<String>(200, null, res);
    }

    @PreAuthorize("hasRole('ROLE_NVGH')")
    @PutMapping(value = "delivery-person/reject/{id}")
    public ApiResponse<String> onReject(@PathVariable("id") Long id) {
                String res = billOfLadingService.onReject(id);
        return new ApiResponse<String>(200, null, res);
    }


}
