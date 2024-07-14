package store.chikendev._2tm.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.PaymentRecordResponse;
import store.chikendev._2tm.entity.PaymentRecords;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.PaymentRecordsRepository;
import store.chikendev._2tm.utils.Payment;

@Service
public class PaymentService {

    @Autowired
    private PaymentRecordsRepository paymentRecordsRepository;

    @Autowired
    private Payment payment;

    public PaymentRecordResponse update(String id, Double amount, String bankCode, String cartType, String bankTranNo,
            String payDate, String status) {
        PaymentRecords record = paymentRecordsRepository.findById(id).orElseThrow(() -> {
            throw new AppException(ErrorCode.PAYMENT_RECORD_NOT_FOUND);
        });
        record.setAmount(amount);
        if (status.equals("00")) {
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
        }
        return convertToPaymentRecordResponse(paymentRecordsRepository.save(record));
    }

    private PaymentRecordResponse convertToPaymentRecordResponse(PaymentRecords record) {
        return PaymentRecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .bankCode(record.getBankCode())
                .cartType(record.getCartType())
                .bankTranNo(record.getBankTranNo())
                .payDate(record.getPayDate())
                .status(record.getStatus() ? "Thanh toán thành công" : "Thanh toán thất bại")
                .account(AccountResponse.builder()
                        .id(record.getAccount().getId())
                        .fullName(record.getAccount().getFullName())
                        .email(record.getAccount().getEmail())
                        .phoneNumber(record.getAccount().getPhoneNumber())
                        .build())
                .build();
    }
}
