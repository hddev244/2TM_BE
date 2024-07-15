package store.chikendev._2tm.service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.PaymentRecordResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.Order;
import store.chikendev._2tm.entity.PaymentRecords;
import store.chikendev._2tm.entity.StateOrder;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.OrderRepository;
import store.chikendev._2tm.repository.PaymentRecordsRepository;
import store.chikendev._2tm.repository.StateOrderRepository;
import store.chikendev._2tm.utils.Payment;

@Service
public class PaymentService {

    @Autowired
    private PaymentRecordsRepository paymentRecordsRepository;

    @Autowired
    private Payment payment;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StateOrderRepository stateOrderRepository;

    @Autowired
    private AccountRepository accountRepository;

    public String payment(String id, String bankCode, String cartType,
            String bankTranNo,
            String payDate, String status) {
        PaymentRecords record = paymentRecordsRepository.findById(id).orElseThrow(() -> {
            throw new AppException(ErrorCode.PAYMENT_RECORD_NOT_FOUND);
        });
        if (status.equals("00")) {
            List<Order> orders = orderRepository.findByPaymentRecord(record);
            if (orders.size() <= 0) {
                throw new AppException(ErrorCode.PAYMENT_RECORD_NOT_FOUND);
            }
            for (Order order : orders) {
                order.setPaymentStatus(true);
                order.setPaymentId(bankTranNo);
                order.setStateOrder(stateOrderRepository.findById(StateOrder.CONFIRMED).get());
            }
            record.setStatus(true);
            record.setBankCode(bankCode);
            record.setCartType(cartType);
            record.setBankTranNo(bankTranNo);
            if (payDate != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date date = sdf.parse(payDate);
                    record.setPayDate(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            orderRepository.saveAllAndFlush(orders);
            paymentRecordsRepository.saveAndFlush(record);
            return "Thanh toán thành công";
        } else {
            throw new AppException(ErrorCode.valueOf("PAYMENT_ERROR_" + status));
        }
    }

    public String resetPayment(String id) throws UnsupportedEncodingException {
        PaymentRecords record = paymentRecordsRepository.findById(id).orElseThrow(() -> {
            throw new AppException(ErrorCode.PAYMENT_RECORD_NOT_FOUND);
        });
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!record.getAccount().getId().equals(account.getId())) {
            throw new AppException(ErrorCode.PAYMENT_RECORD_NOT_FOUND);
        }
        return payment.createVNPT(record.getAmount().longValue(), record.getId());

    }

    public Page<PaymentRecordResponse> getByAllAndStatus(int size, int page, Boolean status) {
        Pageable pageable = PageRequest.of(page, size);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        });

        if (status == null) {
            return paymentRecordsRepository.findByAccount(account, pageable).map(this::convertToPaymentRecordResponse);
        }
        return paymentRecordsRepository.findByAccountAndStatus(account, status, pageable)
                .map(this::convertToPaymentRecordResponse);
    }

    private PaymentRecordResponse convertToPaymentRecordResponse(PaymentRecords record) {
        return PaymentRecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .bankCode(record.getBankCode())
                .cartType(record.getCartType())
                .bankTranNo(record.getBankTranNo())
                .payDate(record.getPayDate())
                .status(record.getStatus() ? "Đã thanh toán" : "Chưa thanh toán")
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .account(AccountResponse.builder()
                        .id(record.getAccount().getId())
                        .fullName(record.getAccount().getFullName())
                        .email(record.getAccount().getEmail())
                        .phoneNumber(record.getAccount().getPhoneNumber())
                        .build())
                .build();
    }
}
