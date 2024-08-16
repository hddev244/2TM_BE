package store.chikendev._2tm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import store.chikendev._2tm.dto.request.ShippingCostRequest;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.ShippingCostResponse;
import store.chikendev._2tm.entity.ShippingCost;
import store.chikendev._2tm.service.ShippingCostService;

@RestController
@RequestMapping("/api/shipping-cost")
public class ShippingCostController {

    @Autowired
    private ShippingCostService shippingCostService;

    @PreAuthorize("hasAnyRole('ROLE_QTV')")
    @PostMapping("/create-ship-cost")
    public ApiResponse<ShippingCostResponse> createShippingCost(@RequestBody ShippingCostRequest request) {
        ShippingCost shippingCost = shippingCostService.createShippingCost(request.getCost());
        ShippingCostResponse response = new ShippingCostResponse();
        response.setId(shippingCost.getId());
        response.setCost(shippingCost.getCost());
        return new ApiResponse<ShippingCostResponse>(200, null, response);
    }

    @PreAuthorize("hasAnyRole('ROLE_QTV')")
    @PutMapping("/update-ship-cost/{id}")
    public ApiResponse<ShippingCostResponse> updateShippingCost(@PathVariable("id") Long id,
            @RequestBody ShippingCostRequest request) {
        ShippingCost shippingCost = shippingCostService.updateShippingCost(id, request.getCost());
        ShippingCostResponse response = new ShippingCostResponse();
        response.setId(shippingCost.getId());
        response.setCost(shippingCost.getCost());
        return new ApiResponse<ShippingCostResponse>(200, null, response);
    }

    @GetMapping
    public ApiResponse<List<ShippingCostResponse>> getAllShippingCosts() {
        return new ApiResponse<List<ShippingCostResponse>>(200, null, shippingCostService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<ShippingCostResponse> getShippingCostById(@PathVariable("id") Long id) {
        ShippingCostResponse response = shippingCostService.getById(id);
        return new ApiResponse<ShippingCostResponse>(200, null, response);
    }

    // tìm phí ship thích hợp
    @GetMapping("/findOne")
    public ApiResponse<ShippingCostResponse> getShippingCost(
            @RequestParam("storeId") Long storeId,
            @RequestParam("wardIdDelivery") Long wardIdDelivery) {
        return new ApiResponse<ShippingCostResponse>(200, null,
                shippingCostService.findShippingCost(storeId, wardIdDelivery));
    }

}
