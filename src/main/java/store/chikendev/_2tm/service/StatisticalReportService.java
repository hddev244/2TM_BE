package store.chikendev._2tm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import store.chikendev._2tm.dto.responce.OrderDetailResponse;
import store.chikendev._2tm.dto.responce.OrderResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.dto.responce.StatisticalReportResponse;
import store.chikendev._2tm.dto.responce.StatisticalReportRevenueResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.AccountStore;
import store.chikendev._2tm.entity.Image;
import store.chikendev._2tm.entity.Order;
import store.chikendev._2tm.entity.OrderDetails;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.Report;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.AccountStoreRepository;
import store.chikendev._2tm.repository.OrderDetailsRepository;
import store.chikendev._2tm.repository.OrderRepository;
import store.chikendev._2tm.repository.StoreRepository;
import store.chikendev._2tm.utils.dtoUtil.response.ImageDtoUtil;

@Service
public class StatisticalReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private AccountStoreRepository accountStoreRepository;

    @Autowired
    private StoreRepository storeRepository;

    // CH - xem báo cáo doanh thu theo ngày truyền vào
    public Page<StatisticalReportResponse> getStatisticalReportByDate(
            String dateString,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        Account account = accountRepository
                .findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        LocalDate date = convertToDate(dateString);
        if (date == null) {
            throw new AppException(ErrorCode.INVAL_DATETIME_INPUT);
        }
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        Page<OrderDetails> orderDetails = orderDetailsRepository.findAllByDate(
                startOfDay,
                endOfDay,
                account,
                pageable);
        return convertToStatisticalReportResponse(orderDetails);
    }

    // CH - xem báo cáo doanh thu theo tháng truyền vào
    public Page<StatisticalReportResponse> getStatisticalReportByMonth(
            String monthStr,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        Account account = accountRepository
                .findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        YearMonth yearMonth = convertToYearMonth(monthStr);
        if (yearMonth == null) {
            throw new AppException(ErrorCode.INVAL_DATETIME_INPUT);
        }
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay();
        Page<OrderDetails> orderDetails = orderDetailsRepository.findAllByMonth(
                startOfMonth,
                endOfMonth,
                account,
                pageable);
        return convertToStatisticalReportResponse(orderDetails);
    }

    // QLCH - xem các order đã hoàn thành trong ngày
    public Page<OrderResponse> getStatisticalReportByDateAndType(
            String dateString,
            Boolean type,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        Account account = accountRepository
                .findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        LocalDate date = convertToDate(dateString);
        if (date == null) {
            throw new AppException(ErrorCode.INVAL_DATETIME_INPUT);
        }
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        Optional<AccountStore> accountStore = accountStoreRepository.findByAccount(account);
        System.out.println(startOfDay + " " + endOfDay + " TC");
        if (type == null) {
            Page<Order> orders = orderRepository.findByDate(
                    startOfDay,
                    endOfDay,
                    accountStore.get().getStore(),
                    pageable);
            return orders.map(order -> {
                return convertToOrderResponse(order);
            });
        } else {
            Page<Order> orders = orderRepository.findByDateAndType(
                    startOfDay,
                    endOfDay,
                    accountStore.get().getStore(),
                    type,
                    pageable);
            return orders.map(order -> {
                return convertToOrderResponse(order);
            });
        }
    }

    // QLCH - xem các order đã hoàn thành trong tháng
    public Page<OrderResponse> getStatisticalReportByMonthAndType(
            String monthStr,
            Boolean type,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        Account account = accountRepository
                .findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        YearMonth yearMonth = convertToYearMonth(monthStr);
        if (yearMonth == null) {
            throw new AppException(ErrorCode.INVAL_DATETIME_INPUT);
        }
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay();
        Optional<AccountStore> accountStore = accountStoreRepository.findByAccount(account);
        if (type == null) {
            Page<Order> orders = orderRepository.findByDate(
                    startOfMonth,
                    endOfMonth,
                    accountStore.get().getStore(),
                    pageable);
            return orders.map(order -> {
                return convertToOrderResponse(order);
            });
        } else {
            Page<Order> orders = orderRepository.findByDateAndType(
                    startOfMonth,
                    endOfMonth,
                    accountStore.get().getStore(),
                    type,
                    pageable);
            return orders.map(order -> {
                return convertToOrderResponse(order);
            });
        }
    }

    // demo Tổng Doanh từ bán sản phẩm của cửa hàng theo ngày.
    public StatisticalReportRevenueResponse getRevenueByDate(
            String dateString) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        Account account = accountRepository
                .findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        LocalDate date = convertToDate(dateString);
        System.out.println(date);
        if (date == null) {
            throw new AppException(ErrorCode.INVAL_DATETIME_INPUT);
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        Optional<AccountStore> accountStore = accountStoreRepository.findByAccount(account);
        System.out.println(accountStore.get().getStore().getId());
        List<Order> orders = orderRepository.findByDateAndTypeNoPage(
                startOfDay,
                endOfDay,
                accountStore.get().getStore(),
                Order.TYPE_ORDER_OF_CUSTOMER);
        Double sumTotalPrice = 0.0;
        // trừ các sản phẩm là ký gửi ra
        for (Order order : orders) {
            Double totalPrice = order.getTotalPrice();
            for (OrderDetails detail : order.getDetails()) {
                if (detail.getProduct().getOwnerId() != null ||
                        detail.getProduct().getType() == Product.TYPE_PRODUCT_OF_ACCOUNT) {
                    totalPrice = totalPrice -
                            (detail.getPrice() * detail.getQuantity());
                }
            }
            sumTotalPrice += totalPrice;
        }
        return StatisticalReportRevenueResponse.builder()
                .Date(dateString)
                .totalSale(sumTotalPrice)
                .build();
    }

    private Page<StatisticalReportResponse> convertToStatisticalReportResponse(
            Page<OrderDetails> orderDetails) {
        return orderDetails.map(orderDetail -> {
            return StatisticalReportResponse.builder()
                    .idProduct(orderDetail.getProduct().getId())
                    .nameProduct(orderDetail.getProduct().getName())
                    .quantitySold(orderDetail.getQuantity())
                    .totalSale(orderDetail.getPrice())
                    .totalAmount(orderDetail.getQuantity() * orderDetail.getPrice())
                    .saleDate(orderDetail.getOrder().getCompleteAt())
                    .build();
        });
    }

    private LocalDate convertToDate(String dateStr) {
        dateStr = dateStr.trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (!dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new AppException(ErrorCode.INVAL_DATETIME_INPUT);
        }
        return LocalDate.parse(dateStr, formatter);
    }

    public YearMonth convertToYearMonth(String monthStr) {
        monthStr = monthStr.trim();
        if (!monthStr.matches("\\d{2}/\\d{4}")) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        return YearMonth.parse(monthStr, formatter);
    }

    private String getAddress(Order order) {
        if (order == null) {
            return "";
        }
        if (order.getWard() != null) {
            String addressWard = order.getWard().getName();
            String addressDistrict = order.getWard().getDistrict().getName();
            String addressProvince = order
                    .getWard()
                    .getDistrict()
                    .getProvinceCity()
                    .getName();
            String addressAddress = order.getConsigneeDetailAddress() == null
                    ? ""
                    : order.getConsigneeDetailAddress() + ", ";
            return (addressAddress +
                    addressWard +
                    ", " +
                    addressDistrict +
                    ", " +
                    addressProvince);
        }
        return "";
    }

    private OrderDetailResponse convertToOrderDetailResponse(
            OrderDetails detail) {
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

        List<ResponseDocumentDto> responseDocument = product
                .getImages()
                .stream()
                .map(img -> {
                    Image image = img.getImage();
                    return ImageDtoUtil.convertToImageResponse(image);
                })
                .toList();
        response.setImages(responseDocument);
        return response;
    }

    private OrderResponse convertToOrderResponse(Order order) {
        if (order == null) {
            return null;
        }
        List<OrderDetails> details = order.getDetails();
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
        if (details != null) {
            orderDetailResponses = details
                    .stream()
                    .map(detail -> {
                        return convertToOrderDetailResponse(detail);
                    })
                    .collect(Collectors.toList());
        }

        String storeName = (order.getStore() != null)
                ? order.getStore().getName()
                : "";

        return OrderResponse.builder()
                .id(order.getId())
                .deliveryCost(
                        order.getDeliveryCost() != null ? order.getDeliveryCost() : 0.0)
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .completeAt(order.getCompleteAt())
                .paymentStatus(order.getPaymentStatus())
                .paymentId(order.getPaymentId())
                .address(getAddress(order))
                .consigneeName(order.getConsigneeName())
                .consigneePhoneNumber(order.getConsigneePhoneNumber())
                .totalPrice(order.getTotalPrice())
                .accountName(
                        order.getAccount() != null
                                ? order.getAccount().getFullName()
                                : "")
                .state(
                        order.getStateOrder() != null
                                ? order.getStateOrder().getStatus()
                                : "")
                .paymentMethodName(
                        order.getPaymentMethod() != null
                                ? order.getPaymentMethod().getName()
                                : "")
                .detail(orderDetailResponses)
                .storeName(storeName)
                .paymentRecordId(
                        order.getPaymentRecord() != null
                                ? order.getPaymentRecord().getId()
                                : "")
                .build();
    }

    public List<Report> getRevenueByMinAndMaxDate(String minDate, String maxDate,
            Long storeId) {
        List<Report> reports = new ArrayList<>();
        LocalDate min = convertToDate(minDate);
        LocalDate max = convertToDate(maxDate);
        List<Object[]> results = new ArrayList<>();
        if (storeId == null) {
            results = orderRepository.getNativeReportByRangeDate(min, max);

        } else {
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));

            results = orderRepository.getNativeReportByRangeDateAndStore(min, max, store.getId());

        }
        reports = getReportsForDateRange(results, minDate, maxDate);
        return reports;
    }

    public Report getRevenueByDate(String dateReq, Long storeId) {
        // LocalDate date = convertToDate(dateReq);
        Report report = new Report();
        LocalDate min = convertToDate(dateReq);
        LocalDate max = min.plusDays(1);

        List<Object[]> results = new ArrayList<>();

        if (storeId == null) {
            results = orderRepository.getNativeReportByRangeDate(min, max);

        } else {
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));

            results = orderRepository.getNativeReportByRangeDateAndStore(min, max, store.getId());

        }
        if (results.size() == 0) {
            return report;
        }

        if (results.size() == 0) {
            return report;
        }
        Object[] result = results.get(0);
        return convertToReport(result);
    }

    private Report convertToReport(Object[] result) {
        String completeAt = result[0].toString(); // Convert to String if necessary
        Double totalSale = (Double) result[1];
        return new Report(completeAt, totalSale);
    }

    // convert string date DD/MM/YYYY to YYYY-MM-DD
    public String convertDate(String date) {
        String[] dateArr = date.split("/");
        return dateArr[2] + "-" + dateArr[1] + "-" + dateArr[0];
    }

    public List<String> generateDateRange(String start, String end) {
        List<String> dates = new ArrayList<>();
        LocalDate startDate = convertToDate(start);
        LocalDate endDate = convertToDate(end);

        if (startDate == null || endDate == null) {
            throw new AppException(ErrorCode.INVAL_DATETIME_INPUT);
        }

        while (!startDate.isAfter(endDate)) {
            dates.add(startDate.toString());
            startDate = startDate.plusDays(1);
        }
        return dates;
    }

    public List<Report> getReportsForDateRange(List<Object[]> results, String start, String end) {
        List<Report> reports = new ArrayList<>();
        List<String> dates = generateDateRange(start, end);
        HashMap<String, Double> map = new HashMap<>();

        for (String date : dates) {
            map.put(date, 0.0);
        }

        for (Object[] result : results) {
            String completeAt = result[0].toString();
            Double totalSale = (Double) result[1];
            map.put(completeAt, totalSale);
        }

        for (String date : dates) {
            reports.add(new Report(date, map.get(date)));
        }

        return reports;
    }
}
