package store.chikendev._2tm.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.BillOfLadingResponse;
import store.chikendev._2tm.service.BillOfLadingService;

@RestController
@RequestMapping("/api/billOfLading")
public class BillOfLadingController {

    @Autowired
    private BillOfLadingService billOfLadingService;

    @PreAuthorize("hasAnyRole('ROLE_QLCH', 'ROLE_NVCH')")
    @PostMapping("/add")
    public ApiResponse<BillOfLadingResponse> addBillOfLading(@RequestParam("idOrder") Long idOrder) {
        BillOfLadingResponse response = billOfLadingService.addBillOfLading(idOrder);
        return new ApiResponse<>(200, null, response);
    }

    @PreAuthorize("hasAnyRole('ROLE_NVGH', 'ROLE_NVCH')")
    @GetMapping("/{deliveryPerson}")
    public ApiResponse<List<BillOfLadingResponse>> getShipList(@PathVariable("deliveryPerson") String deliveryPerson) {
        return new ApiResponse<List<BillOfLadingResponse>>(200, null,
                billOfLadingService.getBillOfLadingByDeliveryPersonId(deliveryPerson));
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

    // @PreAuthorize("isAuthenticated()")
    // @GetMapping("/billOfLadingList")
    // public ApiResponse<List<BillOfLadingResponse>> getAll() {
    // return new ApiResponse<List<BillOfLadingResponse>(200, null,
    // billOfLadingService.getAllBillOfLadings());
    // }

}
