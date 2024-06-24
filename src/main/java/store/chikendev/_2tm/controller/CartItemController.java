package store.chikendev._2tm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.service.CartItemService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.CartResponse;

@RestController
public class CartItemController {
    @Autowired
    private CartItemService cartService;

    @GetMapping("/cart")
    public ApiResponse<List<CartResponse>> getUserCart() {
        return new ApiResponse<List<CartResponse>>(200, null, cartService.getUserCart());
    }

}
