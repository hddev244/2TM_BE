package store.chikendev._2tm.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.OrderInformation;
import store.chikendev._2tm.dto.request.OrderRequest;
import store.chikendev._2tm.dto.responce.OrderDetailResponse;
import store.chikendev._2tm.dto.responce.OrderPaymentResponse;
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
import store.chikendev._2tm.entity.Store;
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
import store.chikendev._2tm.repository.StoreRepository;
import store.chikendev._2tm.repository.WardRepository;
import store.chikendev._2tm.utils.Payment;
import store.chikendev._2tm.utils.SendEmail;
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

    @Autowired
    private SendEmail sendEmail;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private Payment payment;

    public OrderPaymentResponse createOrder(OrderInformation request) throws UnsupportedEncodingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PaymentMethods methods = methodsRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_METHOD_NOT_FOUND));

        Ward ward = wardRepository.findById(request.getWardId())
                .orElseThrow(() -> new AppException(ErrorCode.WARD_NOT_FOUND));

        if (request.getDetails().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        List<OrderResponse> orders = new ArrayList<>();
        Long sumTotalPrice = 0L;

        for (OrderRequest detailRequest : request.getDetails()) {
            Store store = storeRepository.findById(detailRequest.getStoreId())
                    .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));

            List<CartItems> cartItems = new ArrayList<>();
            for (Long id : detailRequest.getCartItemId()) {
                CartItems item = cartItemsRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.CART_EMPTY));

                if (!item.getAccount().getId().equals(account.getId())) {
                    throw new AppException(ErrorCode.CART_EMPTY);
                }

                if (item.getProduct().getStore().getId() != detailRequest.getStoreId()) {
                    throw new AppException(ErrorCode.CART_EMPTY);
                }

                cartItems.add(item);
            }

            if (cartItems.isEmpty()) {
                throw new AppException(ErrorCode.CART_EMPTY);
            }

            Order order = Order.builder()
                    .deliveryCost(Optional.ofNullable(detailRequest.getDeliveryCost()).orElse((double) 0))
                    .note(request.getNote())
                    .paymentStatus(false)
                    .consigneeDetailAddress(request.getConsigneeDetailAddress())
                    .consigneeName(request.getConsigneeName())
                    .consigneePhoneNumber(request.getConsigneePhoneNumber())
                    .account(account)
                    .stateOrder(stateOrderRepository.findById(StateOrder.IN_CONFIRM).get())
                    .paymentMethod(methods)
                    .ward(ward)
                    .store(store)
                    .build();

            Order savedOrder = orderRepository.save(order);

            List<OrderDetails> details = new ArrayList<>();
            double totalPrice = 0.0;

            for (CartItems item : cartItems) {
                OrderDetails detail = OrderDetails.builder()
                        .price(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .product(item.getProduct())
                        .order(savedOrder)
                        .build();

                details.add(detail);

                Product product = detail.getProduct();
                int remainingQuantity = product.getQuantity() - detail.getQuantity();

                if (remainingQuantity < 0) {
                    orderRepository.delete(savedOrder);
                    throw new AppException(ErrorCode.PRODUCT_NOT_ENOUGH);
                }

                product.setQuantity(remainingQuantity);
                productRepository.save(product);
                totalPrice += detail.getPrice() * detail.getQuantity();
            }
            if (detailRequest.getCartItemId().size() != details.size()) {
                orderRepository.delete(savedOrder);
                throw new AppException(ErrorCode.CART_EMPTY);
            }

            orderDetailRepository.saveAll(details);
            cartItemsRepository.deleteAll(cartItems);

            savedOrder.setTotalPrice(totalPrice);
            savedOrder.setDetails(details);
            orderRepository.save(savedOrder);

            OrderResponse response = convertToOrderResponse(savedOrder);
            sumTotalPrice += response.getTotalPrice().longValue();
            orders.add(response);
        }
        OrderPaymentResponse orderPaymentResponse = new OrderPaymentResponse();
        orderPaymentResponse.setOrders(orders);

        String htmlContent = generateOrdersSummaryHtml(orders);
        sendEmail.sendMail(account.getEmail(), "Đơn hàng của bạn đã được tạo", htmlContent);
        if (methods.getId() == PaymentMethods.PAYMENT_ON_DELIVERY) {
            orderPaymentResponse.setSumTotalPrice(sumTotalPrice);
            orderPaymentResponse.setPaymentLink(methods.getName());
        } else {
            orderPaymentResponse.setSumTotalPrice(sumTotalPrice);
            orderPaymentResponse.setPaymentLink(payment.createVNPT(sumTotalPrice));
        }
        return orderPaymentResponse;
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
                .storeName(order.getStore().getName())
                .build();
    }

    private String generateOrdersSummaryHtml(List<OrderResponse> orders) {
        StringBuilder htmlBuilder = new StringBuilder();

        double grandTotal = 0;

        htmlBuilder.append("<html>")
                .append("<head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 20px; }")
                .append(".order-summary { border: 1px solid #ddd; padding: 20px; margin-bottom: 20px; }")
                .append(".order-header { background-color: #f2f2f2; padding: 10px; font-size: 18px; }")
                .append(".order-details { margin-top: 10px; }")
                .append("table { width: 100%; border-collapse: collapse; }")
                .append("th, td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }")
                .append(".total-price, .grand-total { font-weight: bold; margin-top: 10px; }")
                .append(".grand-total { font-size: 20px; margin-top: 20px; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<h1>Tạo thành công các hóa đơn</h1>")
                .append("<p>Cảm ơn bạn đã đặt hàng của chúng tôi!</p>");

        for (OrderResponse order : orders) {
            htmlBuilder.append("<div class='order-summary'>")
                    .append("<div class='order-header'>Hóa đơn: ").append(order.getId()).append("</div>")
                    .append("<div class='order-details'>")
                    .append("<p>Ngày tạo hóa đơn: ").append(order.getCreatedAt()).append("</p>")
                    .append("<p>Hình thức thanh toán: ").append(order.getPaymentMethodName()).append("</p>")
                    .append("<p>Địa chỉ nhận hàng: ").append(order.getAddress()).append("</p>")
                    .append("<p>Sản phẩm đã đặt:</p>")
                    .append("<table>")
                    .append("<tr><th>Tên sản phẩm</th><th>Số lượng</th><th>Đơn giá (VND)</th><th>Tổng tiền (VND)</th></tr>")
                    .append(order.getDetail().stream()
                            .map(item -> "<tr>"
                                    + "<td>" + item.getProduct().getName() + "</td>"
                                    + "<td>" + item.getQuantity() + "</td>"
                                    + "<td>" + item.getPrice() + "</td>"
                                    + "<td>" + (item.getQuantity() * item.getPrice()) + "</td>"
                                    + "</tr>")
                            .collect(Collectors.joining()))
                    .append("</table>")
                    .append("<p class='total-price'>Tổng giá trị: ").append(order.getTotalPrice()).append(" VND</p>")
                    .append("</div>")
                    .append("</div>");

            grandTotal += order.getTotalPrice();
        }

        htmlBuilder.append("<div class='grand-total'>Tổng tiền của tất cả hóa đơn: ").append(grandTotal)
                .append(" VND</div>")
                .append("</body></html>");

        return htmlBuilder.toString();
    }

}
