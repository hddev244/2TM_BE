package store.chikendev._2tm.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.CartResponse;
import store.chikendev._2tm.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ApiResponse<?> addProductToCart(@RequestParam Long idProduct) {
        cartService.addProductToCart(idProduct);
        return new ApiResponse<>(200, Collections.singletonList("Thêm sản phẩm thành công"), null);
    }

    @GetMapping
    public ApiResponse<List<CartResponse>> getUserCart() {
        return new ApiResponse<List<CartResponse>>(200, null, cartService.getUserCart());
    }
}
