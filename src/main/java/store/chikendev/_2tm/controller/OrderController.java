package store.chikendev._2tm.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.OrderInformation;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.OrderPaymentResponse;
import store.chikendev._2tm.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ApiResponse<OrderPaymentResponse> createOder(@RequestBody @Valid OrderInformation request)
            throws UnsupportedEncodingException {
        return new ApiResponse<OrderPaymentResponse>(200, null, orderService.createOrder(request));
    }

}
