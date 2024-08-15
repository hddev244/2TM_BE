package store.chikendev._2tm.controller;

import jakarta.validation.Valid;
import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

        @PreAuthorize("hasAnyRole('ROLE_QLCH', 'ROLE_NVCH')")
        @GetMapping("/getByStore")
        public ApiResponse<Page<OrderResponse>> getByStore(
                        @RequestParam(required = false, name = "stateId") Long stateId,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "10") int size) {
                Page<OrderResponse> orders = orderService.getOrdersByStoreAllOrState(
                                page,
                                size,
                                stateId);

                return new ApiResponse<Page<OrderResponse>>(200, null, orders);
        }

        @PreAuthorize("hasRole('ROLE_KH, ROLE_NVCH, ROLE_QLCH, ROLE_QTV')")
        @GetMapping("/{orderId}")
        public ApiResponse<OrderResponse> getOrderDetails(@PathVariable("orderId") Long orderId) {
                OrderResponse orderResponse = orderService.getOrderDetails(orderId);
                return new ApiResponse<OrderResponse>(200, null, orderResponse);
        }

        @PreAuthorize("hasAnyRole('ROLE_KH')")
        @GetMapping("/state")
        public ApiResponse<Page<OrderResponse>> getOrderByStateId(
                        @RequestParam(name = "size", defaultValue = "10") int size,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(required = false, name = "stateId") Long stateId) {
                Page<OrderResponse> orders = orderService.getOrderByStateId(size, page,
                                stateId);
                return new ApiResponse<Page<OrderResponse>>(200, null, orders);
        }

        @GetMapping("/paid")
        public ApiResponse<Page<OrderResponse>> getPaidOrders(
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "10") int size,
                        @RequestParam(name = "state", required = false) Boolean state) {

                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Page<OrderResponse> orders = orderService.getPaidOrdersByStore(email, state,
                                PageRequest.of(page, size));
                return new ApiResponse<Page<OrderResponse>>(200, null, orders);
        }
}
