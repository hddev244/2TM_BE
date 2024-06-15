package store.chikendev._2tm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.utils.SendOtp;

@Service
public class OtpService {

    @Autowired
    private SendOtp otp;

    public String sendOtp(String number) {
        return otp.sendOtp(number);
    }
}
