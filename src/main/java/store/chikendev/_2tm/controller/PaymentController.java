package store.chikendev._2tm.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.PaymentRecordResponse;
import store.chikendev._2tm.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/test")
    public ResponseEntity<Void> redirectToExternalUrl() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
                URI.create("http://localhost:3001/thanh-toan/thanh-cong/1"));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/{idPaymentRecord}")
    public ResponseEntity<Void> payment(
            @RequestParam("vnp_ResponseCode") String status,
            @PathVariable("idPaymentRecord") String id,
            @RequestParam(name = "vnp_BankTranNo", required = false) String vnpBankTranNo,
            @RequestParam(name = "vnp_PayDate", required = false) String vnpPayDate,
            @RequestParam(name = "vnp_BankCode", required = false) String vnpBankCode,
            @RequestParam(name = "vnp_CardType", required = false) String vnpCardType) {
        paymentService.payment(
                id,
                vnpBankCode,
                vnpCardType,
                vnpBankTranNo,
                vnpPayDate,
                status);
        String url = "http://localhost:3001/thanh-toan/thanh-cong/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/reset/{id}")
    public ApiResponse<String> reset(@PathVariable("id") String id)
            throws UnsupportedEncodingException {
        return new ApiResponse<>(200, null, paymentService.resetPayment(id));
    }

    @GetMapping
    public ApiResponse<Page<PaymentRecordResponse>> getByStateOrAll(
            @RequestParam(required = false, name = "size") Optional<Integer> size,
            @RequestParam(required = false, name = "page") Optional<Integer> page,
            @RequestParam(required = false, name = "status") Boolean status) {
        return new ApiResponse<Page<PaymentRecordResponse>>(
                200,
                null,
                paymentService.getByAllAndStatus(
                        size.orElse(10),
                        page.orElse(0),
                        status));
    }
}
