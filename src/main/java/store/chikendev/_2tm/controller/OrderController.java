package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.chikendev._2tm.dto.request.OrderDetailsRequest;
import store.chikendev._2tm.dto.request.OrderRequest;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.OrderDetailsReponse;
import store.chikendev._2tm.dto.responce.OrderResponse;
import store.chikendev._2tm.service.OrderService;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/addOrder")
    public ApiResponse<OrderResponse> addOrder(@RequestBody OrderRequest request) {
        return new ApiResponse<>(200, null, orderService.addOrder(request));
    }

    @PostMapping("/addOrderDetails")
    public ApiResponse<OrderDetailsReponse> addOrderDetails(@RequestBody OrderDetailsRequest request) {
        return new ApiResponse<>(200, null, orderService.addOrderDetails(request));
    }
}
