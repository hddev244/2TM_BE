package store.chikendev._2tm.controller;

import jakarta.validation.Valid;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.chikendev._2tm.dto.request.OrderInformation;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.OrderPaymentResponse;
import store.chikendev._2tm.dto.responce.OrderResponse;
import store.chikendev._2tm.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ApiResponse<OrderPaymentResponse> createOder(
            @RequestBody @Valid OrderInformation request) throws UnsupportedEncodingException {
        return new ApiResponse<OrderPaymentResponse>(
                200,
                null,
                orderService.createOrder(request));
    }

    @GetMapping("/AllOrder")
    public ApiResponse<Page<OrderResponse>> getAllOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<OrderResponse> ordersPage = orderService.getAllOrders(page, size);
        return new ApiResponse<Page<OrderResponse>>(200, null, ordersPage);
    }

    @PreAuthorize("hasRole('ROLE_KH')")
    @GetMapping("/Ordered")
    public ApiResponse<Page<OrderResponse>> getOrdersForCustomer(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        Page<OrderResponse> orders = (Page<OrderResponse>) orderService.getOrdersByCustomer(email, page, size);

        return new ApiResponse<Page<OrderResponse>>(200, null, orders);
    }

    @PreAuthorize("hasRole('ROLE_KH')")
    @GetMapping("/status")
    public ApiResponse<Page<OrderResponse>> getOrdersByStatusForCustomer(
            @RequestParam Long statusId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        Page<OrderResponse> orders = orderService.getOrdersByStatus(
                statusId,
                email,
                page,
                size);

        return new ApiResponse<Page<OrderResponse>>(200, null, orders);
    }
}
