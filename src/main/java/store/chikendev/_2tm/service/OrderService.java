package store.chikendev._2tm.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.security.SecureRandom;
import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.OrderInformation;
import store.chikendev._2tm.dto.responce.OrderDetailResponse;
import store.chikendev._2tm.dto.responce.OrderPaymentResponse;
import store.chikendev._2tm.dto.responce.OrderResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.AccountStore;
import store.chikendev._2tm.entity.CartItems;
import store.chikendev._2tm.entity.Order;
import store.chikendev._2tm.entity.OrderDetails;
import store.chikendev._2tm.entity.PaymentMethods;
import store.chikendev._2tm.entity.PaymentRecords;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.ShippingCost;
import store.chikendev._2tm.entity.StateOrder;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.entity.Ward;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.AccountStoreRepository;
import store.chikendev._2tm.repository.CartItemsRepository;
import store.chikendev._2tm.repository.DisbursementsRepository;
import store.chikendev._2tm.repository.OrderDetailsRepository;
import store.chikendev._2tm.repository.OrderRepository;
import store.chikendev._2tm.repository.PaymentMethodsRepository;
import store.chikendev._2tm.repository.PaymentRecordsRepository;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.repository.ShippingCostRepository;
import store.chikendev._2tm.repository.StateOrderRepository;
import store.chikendev._2tm.repository.StoreRepository;
import store.chikendev._2tm.repository.WardRepository;
import store.chikendev._2tm.utils.Payment;
import store.chikendev._2tm.utils.SendEmail;
import store.chikendev._2tm.utils.dtoUtil.response.OrderDtoUtil;
import store.chikendev._2tm.utils.service.AccountServiceUtill;

@Service
public class OrderService {

    @Autowired
    private OrderDtoUtil orderDtoUtil;
    @Autowired
    private AccountServiceUtill accountServiceUtill;

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

    @Autowired
    private PaymentRecordsRepository paymentRecordsRepository;

    @Autowired
    private AccountStoreRepository accountStoreRepository;

    @Autowired
    private DisbursementsRepository disbursementsRepository;

    @Autowired
    private ShippingCostRepository shippingCostRepository;

    private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final int ID_LENGTH = 10;

    // random id
    private String generateRandomId() {
        SecureRandom random = new SecureRandom();
        StringBuilder idPaymentRecord = new StringBuilder(ID_LENGTH);

        for (int i = 0; i < ID_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            idPaymentRecord.append(CHARACTERS.charAt(index));
        }
        return idPaymentRecord.toString();
    }

    public OrderPaymentResponse createOrder(OrderInformation request) throws UnsupportedEncodingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PaymentMethods methods = methodsRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_METHOD_NOT_FOUND));

        Ward ward = wardRepository.findById(request.getWardId())
                .orElseThrow(() -> new AppException(ErrorCode.WARD_NOT_FOUND));

        List<Order> savedOrdersList = new ArrayList<>();
        List<CartItems> cartItemsOrderedList = new ArrayList<>();
        List<OrderDetails> savedOrderDetailsList = new ArrayList<>();

        List<OrderResponse> ordersResponseList = new ArrayList<>();

        try {
            request.getDetails().forEach(detail -> {

                Store store = storeRepository.findById(detail.getStoreId()).get();
                if (store == null) {
                    handleOrderError(cartItemsOrderedList, savedOrdersList, savedOrderDetailsList);
                    throw new AppException(ErrorCode.STORE_NOT_FOUND);
                }

                List<CartItems> cartItems = cartItemsRepository.findAllById(detail.getCartItemId());

                if (cartItems.isEmpty() || cartItems.size() != detail.getCartItemId().size()) {
                    handleOrderError(cartItemsOrderedList, savedOrdersList, savedOrderDetailsList);
                    throw new AppException(ErrorCode.CART_EMPTY);
                }

                cartItems.forEach(item -> {
                    if (!item.getAccount().getId().equals(account.getId())) {
                        handleOrderError(cartItemsOrderedList, savedOrdersList, savedOrderDetailsList);
                        throw new AppException(ErrorCode.CART_ITEM_NOT_MATCHING_ACCOUNT);
                    }
                    if (item.getProduct().getStore() == null) {
                        handleOrderError(cartItemsOrderedList, savedOrdersList, savedOrderDetailsList);
                        throw new AppException(ErrorCode.STORE_NOT_FOUND);
                    } else if (item.getProduct().getStore().getId() != store.getId()) {
                        handleOrderError(cartItemsOrderedList, savedOrdersList, savedOrderDetailsList);
                        throw new AppException(ErrorCode.STORE_NOT_FOUND);
                    }
                });

                Order order = Order.builder()
                        .deliveryCost(getShipCost(store.getWard(), ward).getCost())
                        .note(request.getNote())
                        .paymentStatus(false)
                        .consigneeDetailAddress(request.getConsigneeDetailAddress())
                        .consigneeName(request.getConsigneeName())
                        .consigneePhoneNumber(request.getConsigneePhoneNumber())
                        .account(account)
                        .stateOrder(stateOrderRepository.findById(StateOrder.IN_CONFIRM).get())
                        .paymentMethod(methods)
                        .ward(ward)
                        .type(true)
                        .store(store)
                        .build();

                Order savedOrder = orderRepository.save(order);

                OrderResponse orderResponse = orderDtoUtil.convertToOrderResponse(savedOrder);

                Double totalPrice = 0.0;

                List<OrderDetails> orderDetails = new ArrayList<>();
                for (CartItems item : cartItems) {

                    Product product = item.getProduct();

                    int remainingQuantity = product.getQuantity() - item.getQuantity();

                    if (remainingQuantity < 0) {
                        handleOrderError(cartItemsOrderedList, savedOrdersList, savedOrderDetailsList);
                        throw new AppException(ErrorCode.PRODUCT_NOT_ENOUGH);
                    }

                    product.setQuantity(remainingQuantity);
                    product = productRepository.save(product);

                    OrderDetails orderDetail = OrderDetails.builder()
                            .price(item.getProduct().getPrice())
                            .quantity(item.getQuantity())
                            .product(item.getProduct())
                            .order(savedOrder)
                            .build();

                    orderDetail.setOrder(savedOrder);
                    orderDetail = orderDetailRepository.save(orderDetail);
                    orderDetails.add(orderDetail);

                    // lưu đẻ xóa khi thất bại
                    savedOrderDetailsList.add(orderDetail);

                    // lưu để xóa khi thành công
                    cartItemsOrderedList.add(item);

                    totalPrice += item.getProduct().getPrice() * item.getQuantity();
                }

                List<OrderDetailResponse> orderDetailResponses = orderDetails.stream().map(orderDetail -> {
                    return orderDtoUtil.convertToOrderDetailResponse(orderDetail);
                }).collect(Collectors.toList());

                totalPrice += detail.getDeliveryCost();
                savedOrder.setTotalPrice(totalPrice);
                savedOrdersList.add(savedOrder);

                orderResponse.setDetail(orderDetailResponses);
                orderResponse.setTotalPrice(totalPrice);

                ordersResponseList.add(orderResponse);

            });

            return handleOrderSuccess(account, methods, ordersResponseList, cartItemsOrderedList, savedOrdersList);
        } catch (AppException e) {
            handleOrderError(cartItemsOrderedList, savedOrdersList, savedOrderDetailsList);
            throw e;
        }

    }

    public OrderPaymentResponse createOrderRefund(OrderInformation request) throws UnsupportedEncodingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PaymentMethods methods = methodsRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_METHOD_NOT_FOUND));

        Ward ward = wardRepository.findById(request.getWardId())
                .orElseThrow(() -> new AppException(ErrorCode.WARD_NOT_FOUND));

        List<Product> products = new ArrayList<>();

        request.getRefund().forEach(item -> {
            Product product = productRepository.findById(item.getIdProduct())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            if (product.getQuantity() < item.getQuantity()) {
                throw new AppException(ErrorCode.PRODUCT_NOT_ENOUGH);
            }
            if (!account.getId().equals(product.getOwnerId().getId())) {
                throw new AppException(ErrorCode.CART_ITEM_NOT_MATCHING_ACCOUNT);
            }
            products.add(product);
        });
        if (products.size() <= 0 || products.size() != request.getRefund().size()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }
        Store store = products.get(0).getStore();

        products.forEach(product -> {
            if (product.getStore().getId() != store.getId()) {
                throw new AppException(ErrorCode.ORDER_REFUND_ERROR);
            }
        });

        Order order = Order.builder()
                .deliveryCost(0.0)
                .note(request.getNote())
                .paymentStatus(false)
                .consigneeDetailAddress(request.getConsigneeDetailAddress())
                .consigneeName(request.getConsigneeName())
                .consigneePhoneNumber(request.getConsigneePhoneNumber())
                .account(account)
                .stateOrder(stateOrderRepository.findById(StateOrder.IN_CONFIRM).get())
                .paymentMethod(methods)
                .ward(ward)
                .type(false)
                .store(store)
                .build();
        // lưu order đầu
        Order savedOrder = orderRepository.save(order);

        List<OrderDetails> details = new ArrayList<>();
        List<Product> saveProduct = new ArrayList<>();
        products.forEach(product -> {
            OrderDetails detail = OrderDetails.builder()
                    .price((product.getPrice() * product.getProductCommission().getCommissionRate()) / 100)
                    .quantity(request.getRefund().stream()
                            .filter(item -> item.getIdProduct() == product.getId())
                            .findFirst().get().getQuantity())
                    .product(product)
                    .order(savedOrder)
                    .build();
            details.add(detail);
            product.setQuantity(product.getQuantity() - detail.getQuantity());
            saveProduct.add(product);
        });
        Long sumTotalPrice = (long) details.stream().mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                .sum();
        // lưu tổng tiền
        savedOrder.setTotalPrice((double) sumTotalPrice);
        // dữ liệu trả về
        OrderPaymentResponse response = OrderPaymentResponse.builder()
                .sumTotalPrice(sumTotalPrice.longValue())
                .build();
        if (methods.getId() == PaymentMethods.PAYMENT_ON_DELIVERY) {
            response.setPaymentLink(methods.getName());
        } else {
            String idPaymentRecords = generateRandomId();

            while (paymentRecordsRepository.existsById(idPaymentRecords)) {
                idPaymentRecords = generateRandomId();
            }

            PaymentRecords record = PaymentRecords.builder()
                    .id(idPaymentRecords)
                    .account(account)
                    .amount(Double.valueOf(sumTotalPrice))
                    .status(false)
                    .build();
            paymentRecordsRepository.save(record);
            savedOrder.setPaymentRecord(record);

            // lưu order detail và product
            List<OrderDetails> saveOrderDetailsResponse = orderDetailRepository.saveAllAndFlush(details);
            productRepository.saveAll(saveProduct);

            Order saveResponse = orderRepository.save(savedOrder);
            saveResponse.setDetails(saveOrderDetailsResponse);
            response.setOrder(orderDtoUtil.convertToOrderResponse(saveResponse));
            response.setPaymentLink(payment.createVNPT(sumTotalPrice,
                    idPaymentRecords));

            String htmlContent = generateOrderRefundSummaryHtml(orderDtoUtil.convertToOrderResponse(saveResponse));
            sendEmail.sendMail(account.getEmail(), "Đơn hàng hoàn của bạn đã được tạo",
                    htmlContent);
        }
        return response;

    }

    private OrderPaymentResponse handleOrderSuccess(Account account, PaymentMethods method,
            List<OrderResponse> ordersResponseList,
            List<CartItems> cartItemsOrderedList,
            List<Order> savedOrdersList)
            throws UnsupportedEncodingException {
        cartItemsRepository.deleteAll(cartItemsOrderedList);

        Long sumTotalPrice = (long) ordersResponseList.stream().mapToDouble(order -> order.getTotalPrice()).sum();

        OrderPaymentResponse orderPaymentResponse = new OrderPaymentResponse();
        orderPaymentResponse.setSumTotalPrice(sumTotalPrice);

        orderPaymentResponse.setOrders(ordersResponseList);
        String htmlContent = generateOrdersSummaryHtml(ordersResponseList);
        sendEmail.sendMail(account.getEmail(), "Đơn hàng của bạn đã được tạo",
                htmlContent);

        if (method.getId() == PaymentMethods.PAYMENT_ON_DELIVERY) {
            orderPaymentResponse.setPaymentLink(method.getName());
        } else {
            String idPaymentRecords = generateRandomId();

            while (paymentRecordsRepository.existsById(idPaymentRecords)) {
                idPaymentRecords = generateRandomId();
            }

            PaymentRecords record = PaymentRecords.builder()
                    .id(idPaymentRecords)
                    .account(account)
                    .amount(Double.valueOf(sumTotalPrice))
                    .status(false)
                    .build();
            paymentRecordsRepository.save(record);

            savedOrdersList.forEach(order -> {
                order.setPaymentRecord(record);
                orderRepository.save(order);
            });

            orderPaymentResponse.setPaymentLink(payment.createVNPT(sumTotalPrice,
                    idPaymentRecords));
        }
        return orderPaymentResponse;
    }

    private void handleOrderError(
            List<CartItems> cartItemsOrderedList,
            List<Order> savedOrdersList,
            List<OrderDetails> savedOrderDetailsList) {

        cartItemsOrderedList.forEach(cartItem -> {
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() + cartItem.getQuantity());
            productRepository.save(product);
        });

        orderDetailRepository.deleteAll(savedOrderDetailsList);
        orderRepository.deleteAll(savedOrdersList);
    }

    private String generateOrdersSummaryHtml(List<OrderResponse> orders) {
        StringBuilder htmlBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.0");

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
                    .append("<p>Phí ship: ").append(decimalFormat.format(order.getDeliveryCost())).append("</p>")
                    .append("<p>Sản phẩm đã đặt:</p>")
                    .append("<table>")
                    .append("<tr><th>Tên sản phẩm</th><th>Số lượng</th><th>Đơn giá (VND)</th><th>Tổng tiền (VND)</th></tr>")
                    .append(order.getDetail().stream()
                            .map(item -> "<tr>"
                                    + "<td>" + item.getProduct().getName() + "</td>"
                                    + "<td>" + item.getQuantity() + "</td>"
                                    + "<td>" + decimalFormat.format(item.getPrice()) + "</td>"
                                    + "<td>" + decimalFormat.format((item.getQuantity() * item.getPrice())) + "</td>"
                                    + "</tr>")
                            .collect(Collectors.joining()))
                    .append("</table>")
                    .append("<p class='total-price'>Tổng tiền hóa đơn: ")
                    .append(decimalFormat.format(order.getTotalPrice()))
                    .append(" VND</p>")
                    .append("</div>")
                    .append("</div>");

            grandTotal += order.getTotalPrice();
        }

        htmlBuilder.append("<div class='grand-total'>Tổng tiền của tất cả hóa đơn: ")
                .append(decimalFormat.format(grandTotal))
                .append(" VND</div>")
                .append("</body></html>");

        return htmlBuilder.toString();
    }

    private String generateOrderRefundSummaryHtml(OrderResponse order) {
        StringBuilder htmlBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.0");

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

        htmlBuilder.append("<div class='order-summary'>")
                .append("<div class='order-header'>Hóa đơn: ").append(order.getId()).append("</div>")
                .append("<div class='order-details'>")
                .append("<p>Ngày tạo hóa đơn: ").append(order.getCreatedAt()).append("</p>")
                .append("<p>Hình thức thanh toán: ").append(order.getPaymentMethodName()).append("</p>")
                .append("<p>Địa chỉ nhận hàng: ").append(order.getAddress()).append("</p>")
                .append("<p>Phí ship: ").append(decimalFormat.format(order.getDeliveryCost())).append("</p>")
                .append("<p>Sản phẩm đã đặt:</p>")
                .append("<table>")
                .append("<tr><th>Tên sản phẩm</th><th>Số lượng</th><th>Đơn giá (VND)</th><th>Tổng tiền (VND)</th></tr>")
                .append(order.getDetail().stream()
                        .map(item -> "<tr>"
                                + "<td>" + item.getProduct().getName() + "</td>"
                                + "<td>" + item.getQuantity() + "</td>"
                                + "<td>" + decimalFormat.format(item.getPrice()) + "</td>"
                                + "<td>" + decimalFormat.format((item.getQuantity() * item.getPrice())) + "</td>"
                                + "</tr>")
                        .collect(Collectors.joining()))
                .append("</table>")
                .append("<p class='total-price'>Tổng tiền hóa đơn: ")
                .append(decimalFormat.format(order.getTotalPrice()))
                .append(" VND</p>")
                .append("</div>")
                .append("</div>")
                .append("</body></html>");

        return htmlBuilder.toString();
    }

    public Page<OrderResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findAll(pageable);

        return orderPage.map(this::convertToOrderResponse);
    }

    public Page<OrderResponse> getOrdersByCustomer(String email, int page, int size) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> ordersPage = orderRepository.findByAccountId(account.getId(), pageable);

        return ordersPage.map(this::convertToOrderResponse);
    }

    public Page<OrderResponse> getOrdersByStatus(Long statusId, String email, int page, int size) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> ordersPage = orderRepository.findByAccountIdAndStateOrderId(account.getId(), statusId, pageable);

        return ordersPage.map(this::convertToOrderResponse);
    }

    public OrderResponse getDetail(Long idOder) {
        Order order = orderRepository.findById(idOder)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return convertToOrderResponse(order);

    }

    public Page<OrderResponse> getOrdersByStoreAllOrState(int page, int size, Long stateId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        AccountStore store = accountStoreRepository.findByAccount(account).get();
        Pageable pageable = PageRequest.of(page, size);
        if (stateId == null) {
            Page<Order> ordersPage = orderRepository.findByStore(store.getStore(), pageable);
            return ordersPage.map(this::convertToOrderResponse);
        } else {
            StateOrder state = stateOrderRepository.findById(stateId).orElseThrow(() -> {
                return new AppException(ErrorCode.STATE_ORDER_NOT_FOUND);
            });
            Page<Order> ordersPage = orderRepository.findByStoreAndStateId(store.getStore(), state, pageable);
            return ordersPage.map(this::convertToOrderResponse);
        }

    }

    public OrderResponse getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        OrderResponse orderResponse = convertToOrderResponse(order);

        List<OrderDetails> orderDetailsList = orderDetailRepository.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses = orderDetailsList.stream()
                .map(this::convertToOrderDetailResponse)
                .collect(Collectors.toList());

        orderResponse.setDetail(orderDetailResponses);

        return orderResponse;
    }

    public Page<OrderResponse> getOrderByStateId(int size, int page, Long stateId) {
        Account account = accountServiceUtill.getAccount();

        Pageable pageable = PageRequest.of(page, size);
        if (stateId == null) {
            Page<Order> ordersPage = orderRepository.findByAccountIdAndType(account, pageable);
            return ordersPage.map(this::convertToOrderResponse);
        } else {
            StateOrder state = stateOrderRepository.findById(stateId).orElseThrow(() -> {
                return new AppException(ErrorCode.STATE_ORDER_NOT_FOUND);
            });
            Page<Order> ordersPage = orderRepository.findByAccountIdAndStateId(account, state, pageable);
            return ordersPage.map(this::convertToOrderResponse);
        }
    }

    public Page<OrderResponse> getPaidOrdersByStore(String email, Boolean state, Pageable pageable) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Long> orderIds = disbursementsRepository.findOrderIdsByPaymentClerkAndState(account.getId(), state);
        return orderRepository.findByIdIn(orderIds, pageable).map(this::convertToOrderResponse);
    }

    private OrderResponse convertToOrderResponse(Order order) {
        return orderDtoUtil.convertToOrderResponse(order);
    }

    private OrderDetailResponse convertToOrderDetailResponse(OrderDetails detail) {
        return orderDtoUtil.convertToOrderDetailResponse(detail);
    }

    private ShippingCost getShipCost(Ward wardStore, Ward wardDelivery) {
        if (wardStore.getId() == wardDelivery.getId()) {
            ShippingCost shippingCost = shippingCostRepository.findById(ShippingCost.IN_THE_WARD).get();
            return shippingCost;
        }
        if (wardStore.getDistrict().getId() == wardDelivery.getDistrict().getId()) {
            ShippingCost shippingCost = shippingCostRepository.findById(ShippingCost.IN_THE_DISTRICT).get();
            return shippingCost;
        }
        ShippingCost shippingCost = shippingCostRepository.findById(ShippingCost.OUTSIDE_THE_DISTRICT).get();
        return shippingCost;
    }

}
