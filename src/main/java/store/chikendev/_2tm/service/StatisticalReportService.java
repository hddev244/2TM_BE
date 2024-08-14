package store.chikendev._2tm.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.StatisticalReportResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.Order;
import store.chikendev._2tm.entity.OrderDetails;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.OrderDetailsRepository;
import store.chikendev._2tm.repository.OrderRepository;

@Service
public class StatisticalReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    // CH - xem báo cáo doanh thu theo ngày truyền vào
    public Page<StatisticalReportResponse> getStatisticalReportByDate(String dateString, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Page<OrderDetails> orderDetails = orderDetailsRepository.findByCompleteAtAndOwnerId(dateString, account,
                pageable);
        return convertToStatisticalReportResponse(orderDetails);

    }

    private Page<StatisticalReportResponse> convertToStatisticalReportResponse(Page<OrderDetails> orderDetails) {
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

}
