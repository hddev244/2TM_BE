package store.chikendev._2tm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.OrderRequest;
import store.chikendev._2tm.dto.responce.OrderDetailResponse;
import store.chikendev._2tm.dto.responce.OrderResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.CartItems;
import store.chikendev._2tm.entity.Image;
import store.chikendev._2tm.entity.Order;
import store.chikendev._2tm.entity.OrderDetails;
import store.chikendev._2tm.entity.PaymentMethods;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.StateOrder;
import store.chikendev._2tm.entity.Ward;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.CartItemsRepository;
import store.chikendev._2tm.repository.OrderDetailsRepository;
import store.chikendev._2tm.repository.OrderRepository;
import store.chikendev._2tm.repository.PaymentMethodsRepository;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.repository.StateOrderRepository;
import store.chikendev._2tm.repository.WardRepository;
import store.chikendev._2tm.utils.dtoUtil.response.ImageDtoUtil;

@Service
public class OrderService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsRepository orderDetailRepository;

    @Autowired
    private StateOrderRepository stateOrderRepository;

    @Autowired
    private PaymentMethodsRepository methodsRepository;

    @Autowired
    private WardRepository wardRepository;

    @Autowired
    private ProductRepository productRepository;

    public OrderResponse createOrder(OrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        });
        PaymentMethods methods = methodsRepository.findById(request.getPaymentMethodId()).orElseThrow(() -> {
            throw new AppException(ErrorCode.PAYMENT_METHOD_NOT_FOUND);
        });
        Ward ward = wardRepository.findById(request.getWardId()).orElseThrow(() -> {
            throw new AppException(ErrorCode.WARD_NOT_FOUND);
        });
        List<CartItems> cartItem = new ArrayList<>();
        for (Long id : request.getCartItemId()) {
            CartItems item = cartItemsRepository.findById(id).orElseThrow(() -> {
                throw new AppException(ErrorCode.CART_EMPTY);
            });
            if (!item.getAccount().getId().equals(account.getId())) {
                throw new AppException(ErrorCode.CART_EMPTY);
            }
            cartItem.add(item);
        }
        if (cartItem.isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }
        Order order = Order.builder()
                .deliveryCost(request.getDeliveryCost() == null ? 0 : request.getDeliveryCost())
                .note(request.getNote())
                .paymentStatus(false)
                .consigneeDetailAddress(request.getConsigneeDetailAddress())
                .consigneeName(request.getConsigneeName())
                .consigneePhoneNumber(request.getConsigneePhoneNumber())
                .account(account)
                .stateOrder(stateOrderRepository.findById(StateOrder.IN_CONFIRM).get())
                .paymentMethod(methods)
                .ward(ward)
                .build();
        Order save1 = orderRepository.save(order);

        List<OrderDetails> details = new ArrayList<>();
        Double totalPrice = 0.0;
        for (CartItems item : cartItem) {
            OrderDetails detail = OrderDetails.builder()
                    .price(item.getProduct().getPrice())
                    .quantity(item.getQuantity())
                    .product(item.getProduct())
                    .order(save1)
                    .build();
            details.add(detail);
            Product product = detail.getProduct();
            Integer quanTity = product.getQuantity() - detail.getQuantity();
            if (quanTity < 0) {
                orderRepository.delete(save1);
                throw new AppException(ErrorCode.PRODUCT_NOT_ENOUGH);
            }
            product.setQuantity(quanTity);
            System.out.println(quanTity);
            productRepository.save(product);
            totalPrice += detail.getPrice() * detail.getQuantity();
        }
        orderDetailRepository.saveAll(details);
        cartItemsRepository.deleteAll(cartItem);
        save1.setTotalPrice(totalPrice);
        Order save2 = orderRepository.save(save1);
        save2.setDetails(details);
        return convertToOrderResponse(save2);

    }

    private String getAddress(Order order) {
        if (order == null) {
            return "";
        }
        if (order.getWard() != null) {
            String addressWard = order.getWard().getName();
            String addressDistrict = order.getWard().getDistrict().getName();
            String addressProvince = order.getWard().getDistrict().getProvinceCity().getName();
            String addressAddress = order.getConsigneeDetailAddress() == null ? ""
                    : order.getConsigneeDetailAddress() + ", ";
            return addressAddress + addressWard + ", " + addressDistrict + ", " +
                    addressProvince;
        }
        return "";

    }

    private OrderDetailResponse convertToOrderDetailResponse(OrderDetails detail) {
        return OrderDetailResponse.builder()
                .id(detail.getId())
                .price(detail.getPrice())
                .quantity(detail.getQuantity())
                .product(convertToProductResponse(detail.getProduct()))
                .build();
    }

    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setDescription(product.getDescription());
        if (product.getType() != null) {
            response.setTypeProduct(product.getType() ? "Cửa hàng" : "Ký gửi");
        }

        List<ResponseDocumentDto> responseDocument = product.getImages().stream().map(img -> {
            Image image = img.getImage();
            return ImageDtoUtil.convertToImageResponse(image);
        }).toList();
        response.setImages(responseDocument);
        return response;
    }

    private OrderResponse convertToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .deliveryCost(order.getDeliveryCost())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .completeAt(order.getCompleteAt())
                .paymentStatus(order.getPaymentStatus())
                .paymentId(order.getPaymentId())
                .address(getAddress(order))
                .consigneeName(order.getConsigneeName())
                .consigneePhoneNumber(order.getConsigneePhoneNumber())
                .totalPrice(order.getTotalPrice())
                .accountName(order.getAccount().getFullName())
                .state(order.getStateOrder().getStatus())
                .paymentMethodName(order.getPaymentMethod().getName())
                .detail(order.getDetails().stream().map(detail -> {
                    return convertToOrderDetailResponse(detail);
                }).collect(Collectors.toList()))
                .build();
    }
}
