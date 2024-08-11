package store.chikendev._2tm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.DisbursementsResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.Disbursements;
import store.chikendev._2tm.entity.OrderDetails;
import store.chikendev._2tm.entity.PaymentDisbursement;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.DisbursementsRepository;
import store.chikendev._2tm.repository.OrderDetailsRepository;
import store.chikendev._2tm.repository.PaymentDisbursementRepository;

@Service
public class DisbursementsService {
    @Autowired
    private DisbursementsRepository disbursementRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private PaymentDisbursementRepository paymentDisbursementRepository;

    public List<DisbursementsResponse> findbyDisbursementsByAccountAndState(Boolean state) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (state == null) {
            List<Disbursements> disbursementses = disbursementRepository.findDisbursementsByOwnerId(account.getId());
            return disbursementses.stream().map(this::getResponse).collect(Collectors.toList());
        } else {
            List<Disbursements> disbursements = disbursementRepository.findDisbursementsByOwnerIdAndState(
                    account.getId(),
                    state);
            return disbursements.stream().map(this::getResponse).collect(Collectors.toList());
        }
    }

    public DisbursementsResponse getResponse(Disbursements disbursements) {
        OrderDetails orderDetails = orderDetailsRepository.findById(disbursements.getOrderDetail().getId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        Account account = accountRepository.findById(disbursements.getPaymentClerk().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PaymentDisbursement paymentDisbursement = paymentDisbursementRepository
                .findById(disbursements.getPaymentDisbursement().getId())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_DISBURSEMENT_NOT_FOUND));
        DisbursementsResponse response = new DisbursementsResponse();
        response.setPayoutAt(disbursements.getPayoutAt());
        response.setState(disbursements.getState());
        response.setCommissionRate(disbursements.getCommissionRate());
        response.setOrderDetail(orderDetails.getId());
        response.setPaymentClerk(account.getFullName());
        response.setPaymentDisbursement(paymentDisbursement.getId());
        return response;

    }
}
