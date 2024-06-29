package store.chikendev._2tm.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("create-vnpt")
    public ApiResponse<String> create_vnpt(@RequestParam("amount") Integer amount) throws UnsupportedEncodingException {
        return new ApiResponse<>(200, null, paymentService.createVNPT(amount));
    }

    @GetMapping("success")
    public ApiResponse<String> success() {
        return new ApiResponse<>(200, null, "Thanh toán thành công");
    }
}
