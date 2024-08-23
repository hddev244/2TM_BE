package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.PaymentRecordResponse;
import store.chikendev._2tm.service.PaymentRecordService;

@RestController
@RequestMapping("/api/payment-record")
public class PaymentRecordController {

    @Autowired
    private PaymentRecordService paymentRecordService;
  

    @GetMapping("/all-not-paid-yet")
    public ApiResponse<Page<PaymentRecordResponse>> getAllNotPaidYet(
        @RequestParam(name = "pageNo", required = false, defaultValue = "0") Integer pageNo,
        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
        @RequestParam(name = "sort", required = false, defaultValue = "ASC") String sort
    ) {
        return new ApiResponse<Page<PaymentRecordResponse>>(
            200,
            null,
            paymentRecordService.getAllNotPaidYet(pageNo, size)
        );
    }

    @GetMapping("/find-one/{id}")
    public ApiResponse<PaymentRecordResponse> findOne(
        @PathVariable(name = "id") String id
    ) {
        return new ApiResponse<PaymentRecordResponse>(
            200,
            null,
            paymentRecordService.findOne(id)
        );
    }
}
