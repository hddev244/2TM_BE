package store.chikendev._2tm.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.OrderResponse;
import store.chikendev._2tm.dto.responce.PaymentRecordResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.PaymentRecords;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.PaymentRecordsRepository;
import store.chikendev._2tm.utils.dtoUtil.response.OrderDtoUtil;
import store.chikendev._2tm.utils.service.AccountServiceUtill;

@Service
public class PaymentRecordService {

    @Autowired
    private PaymentRecordsRepository paymentRecordsRepository;
    @Autowired
    private AccountServiceUtill accountServiceUtill;
    @Autowired
    private OrderDtoUtil orderDtoUtil;

    public Page<PaymentRecordResponse> getAllNotPaidYet(Integer pageNo, Integer size) {
        Account account = accountServiceUtill.getAccount();

        Pageable pageable = PageRequest.of(pageNo, size);
        Page<PaymentRecords> paymentRecords = paymentRecordsRepository.getAllNotPaidYet(account, pageable);

        Page<PaymentRecordResponse> res = paymentRecords.map(paymentRecord -> {
            AccountResponse accountResponse = null;
            if (paymentRecord.getAccount() != null) {
                accountResponse = AccountResponse.builder()
                        .id(paymentRecord.getAccount().getId())
                        .fullName(paymentRecord.getAccount().getFullName())
                        .build();
            }

            List<OrderResponse> orderDetailResponses = null;
            if (paymentRecord.getOrders() != null) {
                orderDetailResponses = paymentRecord.getOrders().stream().map(order -> {
                    return orderDtoUtil.convertToOrderResponse(order);
                }).toList();
            }
            return PaymentRecordResponse.builder()
                    .id(paymentRecord.getId())
                    .amount(paymentRecord.getAmount() != null ? paymentRecord.getAmount() : 0)
                    .account(accountResponse)
                    .orders(orderDetailResponses)
                    .createdAt(paymentRecord.getCreatedAt())
                    .updatedAt(paymentRecord.getUpdatedAt())
                    .build();
        });

        return res;
    }

    public PaymentRecordResponse findOne(String id) {
        Account account = accountServiceUtill.getAccount();

        PaymentRecords paymentRecord = paymentRecordsRepository.findByIdAndAccount(id, account)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_RECORD_NOT_FOUND));

        AccountResponse accountResponse = null;
        if (paymentRecord.getAccount() != null) {
            accountResponse = AccountResponse.builder()
                    .id(paymentRecord.getAccount().getId())
                    .fullName(paymentRecord.getAccount().getFullName())
                    .build();
        }

        List<OrderResponse> orderDetailResponses = null;
        if (paymentRecord.getOrders() != null) {
            orderDetailResponses = paymentRecord.getOrders().stream().map(order -> {
                return orderDtoUtil.convertToOrderResponse(order);
            }).toList();
        }
        return PaymentRecordResponse.builder()
                .id(paymentRecord.getId())
                .amount(paymentRecord.getAmount() != null ? paymentRecord.getAmount() : 0)
                .account(accountResponse)
                .orders(orderDetailResponses)
                .createdAt(paymentRecord.getCreatedAt())
                .isPaid(paymentRecord.getStatus())
                .updatedAt(paymentRecord.getUpdatedAt())
                .build();
    }
}
