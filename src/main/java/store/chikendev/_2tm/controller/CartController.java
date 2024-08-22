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
    public ApiResponse<CartResponse> addProductToCart(
        @RequestParam(name="productId") Long productId, 
        @RequestParam(name="quantity", required = false, defaultValue = "1") Integer quantity){
            CartResponse res = cartService.addProductToCart(productId,quantity);
        return new ApiResponse<CartResponse>(200, Collections.singletonList("Thêm sản phẩm thành công"), res);
    }

    @PreAuthorize("hasAnyRole('ROLE_KH', 'ROLE_CH')")
    @GetMapping
    public ApiResponse<List<CartResponse>> getUserCart() {
        return new ApiResponse<List<CartResponse>>(200, null, cartService.getUserCart());
    }

    @PreAuthorize("hasAnyRole('ROLE_KH')")
    @DeleteMapping("/remove")
    public ApiResponse<?> removeProductFromCart(
            @RequestParam(name="productId") Long productId) {
        cartService.removeProductFromCart(productId);
        return new ApiResponse<>(200, Collections.singletonList("Sản phẩm đã được xóa khỏi giỏ hàng"), null);
    }

    
}
