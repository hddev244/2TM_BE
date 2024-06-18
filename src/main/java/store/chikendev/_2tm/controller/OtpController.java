package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.dto.request.OtpRequest;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.OtpResponse;
import store.chikendev._2tm.service.OtpService;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/send")
    public ApiResponse<OtpResponse> sendOtp(@RequestParam("input") String input) {
        return new ApiResponse<OtpResponse>(200, null, otpService.sendOtp(input));
    }

    @PostMapping("/confirm")
    public ApiResponse<String> confirmOtp(@RequestBody OtpRequest otp) {
        return new ApiResponse<String>(200, null, otpService.validateOtp(otp));
    }

}
