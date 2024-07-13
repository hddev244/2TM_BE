package store.chikendev._2tm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.repository.PaymentRecordsRepository;
import store.chikendev._2tm.utils.Payment;

@Service
public class PaymentService {

    @Autowired
    private PaymentRecordsRepository paymentRecordsRepository;

    @Autowired
    private Payment payment;
}
