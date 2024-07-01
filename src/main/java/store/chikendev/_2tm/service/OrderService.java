package store.chikendev._2tm.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.OrderDetailsRequest;
import store.chikendev._2tm.dto.request.OrderRequest;
import store.chikendev._2tm.dto.responce.OrderDetailsReponse;
import store.chikendev._2tm.dto.responce.OrderResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.CartItems;
import store.chikendev._2tm.entity.Order;
import store.chikendev._2tm.entity.OrderDetails;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.BillOfLadingRepository;
import store.chikendev._2tm.repository.CartItemsRepository;
import store.chikendev._2tm.repository.OrderDetailsRepository;
import store.chikendev._2tm.repository.OrderRepository;
import store.chikendev._2tm.repository.PaymentMethodsRepository;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.repository.StateOrderRepository;
import store.chikendev._2tm.repository.WardRepository;

@Service
public class OrderService {
        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private OrderDetailsRepository orderDetailsRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private ProductService productService;

        @Autowired
        private AccountRepository accountRepository;

        @Autowired
        private StateOrderRepository stateOrderRepository;

        @Autowired
        private PaymentMethodsRepository paymentMethodRep;

        @Autowired
        private WardRepository wardRep;

        @Autowired
        private BillOfLadingRepository billRep;

        @Autowired
        private CartItemsRepository cartItemsRep;

        public OrderResponse addOrder(OrderRequest request) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByEmail(email)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                Order order = new Order();
                order.setConsigneeName(request.getConsigneeName());
                order.setConsigneePhoneNumber(request.getConsigneePhoneNumber());
                order.setConsigneeDetailAddress(request.getConsigneeDetailAddress());
                order.setDeliveryCost(request.getDeliveryCost());
                order.setTotalPrice(request.getTotalPrice());
                order.setNote(request.getNote());
                order.setPaymentStatus(request.getPaymentStatus());
                order.setAccount(account);
                order.setStateOrder(stateOrderRepository.findById(request.getStateOrder())
                                .orElseThrow(() -> new AppException(ErrorCode.STATE_NOT_FOUND)));
                order.setWard(wardRep.findById(request.getWard())
                                .orElseThrow(() -> new AppException(ErrorCode.WARD_NOT_FOUND)));
                Order save = orderRepository.save(order);
                if (save != null) {
                        List<CartItems> items = cartItemsRep.getItemsByAccount(account.getId());
                        cartItemsRep.deleteAll(items);
                }

                return OrderResponse.builder()
                                .deliveryCost(save.getDeliveryCost())
                                .note(save.getNote())
                                .paymentStatus(save.getPaymentStatus())
                                .paymentId(save.getPaymentId())
                                .consigneeDetailAddress(getAddress(save))
                                .consigneeName(save.getConsigneeName())
                                .consigneePhoneNumber(save.getConsigneePhoneNumber())
                                .totalPrice(save.getTotalPrice())
                                .stateOrder(save.getStateOrder().getStatus())
                                .paymentMethod(save.getPaymentMethod().getName())
                                .build();

        }

        private String getAddress(Order order) {
                if (order == null) {
                        return "";
                }
                if (order.getWard() != null) {
                        String orderWard = order.getWard().getName();
                        String orderDistrict = order.getWard().getDistrict().getName();
                        String orderProvince = order.getWard().getDistrict().getProvinceCity().getName();
                        String orderAddress = order.getConsigneeDetailAddress() == null ? ""
                                        : order.getConsigneeDetailAddress() + ", ";
                        return orderAddress + orderWard + ", " + orderDistrict + ", " + orderProvince;
                }
                return "";

        }

        public OrderDetailsReponse addOrderDetails(OrderDetailsRequest request) {
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setQuantity(request.getQuantity());
                orderDetails.setPrice(request.getPrice());
                OrderDetails saveOrD = orderDetailsRepository.save(orderDetails);
                return OrderDetailsReponse.builder()
                                .product(productService.getById(saveOrD.getProduct().getId()))
                                .quantity(saveOrD.getQuantity())
                                .price(saveOrD.getPrice())
                                .build();
        }

}
