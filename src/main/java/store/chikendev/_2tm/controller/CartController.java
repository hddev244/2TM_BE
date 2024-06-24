package store.chikendev._2tm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.chikendev._2tm.dto.request.CartItemRequest;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.CartResponse;
import store.chikendev._2tm.entity.CartItems;
import store.chikendev._2tm.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartItems> addProductToCart(@RequestBody CartItemRequest request) {
        CartItems cartItem = cartService.addProductToCart(request);
        return ResponseEntity.ok(cartItem);
    }

    @GetMapping
    public ApiResponse<List<CartResponse>> getUserCart() {
        return new ApiResponse<List<CartResponse>>(200, null, cartService.getUserCart());
    }
}
