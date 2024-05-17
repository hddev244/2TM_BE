package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.VerificationCheck;

import store.chikendev._2tm.config.TwilioConfig;

@RestController
@RequestMapping
public class TestController {
    @Autowired
    TwilioConfig twilioConfig;
    
    @GetMapping
    public String test() {
        return "Hello, World!";
    }

    @GetMapping("/otp")
    public String test(@RequestParam("phone") String phone) {

        // // Find your Account Sid and Token at twilio.com/console
        Twilio.init(twilioConfig.getACCOUNT_SID(), twilioConfig.getAUTH_TOKEN());
        VerificationCheck verificationCheck = VerificationCheck.creator(
                "VA08674de01e96b3ff38853f03f6b33342")
                .setTo("+84847511175")
                .setCode("123456")
                .create();
        System.out.println(verificationCheck.getValid());

        System.out.println(twilioConfig.getACCOUNT_SID());

        return "Hello, World!" + phone; 
    }
}
