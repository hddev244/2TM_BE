package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.ProductStoreResponse;
import store.chikendev._2tm.service.ProductStoreService;

@RestController
@RequestMapping("/api/store/product")
public class ProductStoreController {
    @Autowired
    private ProductStoreService productStoreService;

    @GetMapping("/{id}")
    public ApiResponse<ProductStoreResponse> getProduct(@PathVariable("id") Long id) {
        return new ApiResponse<ProductStoreResponse>(200, null, productStoreService.getById(id));
    }

}
