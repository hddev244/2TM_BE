package store.chikendev._2tm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.OrderInformation;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.OrderResponse;
import store.chikendev._2tm.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ApiResponse<List<OrderResponse>> createOder(@RequestBody @Valid OrderInformation request) {
        return new ApiResponse<List<OrderResponse>>(200, null, orderService.createOrder(request));
    }

    @GetMapping
    public OrderInformation demo() {
        return new OrderInformation();
    }

}
