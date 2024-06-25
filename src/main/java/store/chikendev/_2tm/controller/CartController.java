package store.chikendev._2tm.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.CartResponse;
import store.chikendev._2tm.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PreAuthorize("hasAnyRole('ROLE_KH')")
    @PostMapping("/add")
    public ApiResponse<?> addProductToCart(@RequestParam Long idProduct) {
        cartService.addProductToCart(idProduct);
        return new ApiResponse<>(200, Collections.singletonList("Thêm sản phẩm thành công"), null);
    }

    @PreAuthorize("hasAnyRole('ROLE_KH', 'ROLE_CH')")
    @GetMapping
    public ApiResponse<List<CartResponse>> getUserCart() {
        return new ApiResponse<List<CartResponse>>(200, null, cartService.getUserCart());
    }
}
