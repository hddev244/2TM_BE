package store.chikendev._2tm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.dto.request.BillOfLadingRequest;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.BillOfLadingResponse;
import store.chikendev._2tm.service.BillOfLadingService;

@RestController
public class BillOfLadingController {

    @Autowired
    private BillOfLadingService billOfLadingService;

    @PreAuthorize("hasAnyRole('ROLE_QLCH', 'ROLE_NVCH')")
    @PostMapping("/addBillOfLading")
    public ApiResponse<BillOfLadingResponse> addBillOfLading(@RequestBody
    BillOfLadingRequest request) {
    BillOfLadingResponse response = billOfLadingService.addBillOfLading(request);
    System.out.println(response);
    return new ApiResponse<>(200, null, response);
    }

    @PreAuthorize("hasAnyRole('ROLE_NVGH', 'ROLE_NVCH')")
    @GetMapping("/shipList/{deliveryPerson}")
    public ApiResponse<List<BillOfLadingResponse>>
    getShipList(@PathVariable("deliveryPerson") String deliveryPerson) {
    return new ApiResponse<List<BillOfLadingResponse>>(200, null,
    billOfLadingService.getBillOfLadingByDeliveryPersonId(deliveryPerson));
    }

    // @PreAuthorize("isAuthenticated()")
    // @GetMapping("/billOfLadingList")
    // public ApiResponse<List<BillOfLadingResponse>> getAll() {
    // return new ApiResponse<List<BillOfLadingResponse>(200, null, billOfLadingService.getAllBillOfLadings());
    // }

}
