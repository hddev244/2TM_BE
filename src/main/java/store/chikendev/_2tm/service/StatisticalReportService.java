package store.chikendev._2tm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

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
        LocalDate date = convertToDate(dateString);
        if (date == null) {
            throw new AppException(ErrorCode.INVAL_DATETIME_INPUT);
        }
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        Page<OrderDetails> orderDetails = orderDetailsRepository.findAllByDate(startOfDay, endOfDay, account, pageable);
        return convertToStatisticalReportResponse(orderDetails);

    }

    // CH - xem báo cáo doanh thu theo tháng truyền vào
    public Page<StatisticalReportResponse> getStatisticalReportByMonth(String monthStr, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        YearMonth yearMonth = convertToYearMonth(monthStr);
        if (yearMonth == null) {
            throw new AppException(ErrorCode.INVAL_DATETIME_INPUT);
        }
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.plusMonths(1).atDay(1).atStartOfDay();
        Page<OrderDetails> orderDetails = orderDetailsRepository.findAllByMonth(startOfMonth, endOfMonth, account,
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

    private LocalDate convertToDate(String dateStr) {
        dateStr = dateStr.trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (!dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return null;
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

}
