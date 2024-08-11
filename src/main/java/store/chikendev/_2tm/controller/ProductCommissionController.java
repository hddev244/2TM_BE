package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.entity.ProductCommission;
import store.chikendev._2tm.service.ProductCommissionService;

@RestController
@RequestMapping("/api/commissions")
public class ProductCommissionController {
        @Autowired
    private ProductCommissionService productCommissionService;

    @PreAuthorize("hasAnyRole('ROLE_QTV')")
    @PostMapping("/create_commissions")
    public ApiResponse<ProductCommission> createProductCommission(@RequestBody Double commissionRate) {
        ProductCommission productCommission = productCommissionService.addProductCommission(commissionRate);
        return new ApiResponse<ProductCommission>(200, null, productCommission);
    }
}
