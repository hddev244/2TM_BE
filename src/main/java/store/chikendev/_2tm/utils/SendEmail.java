package store.chikendev._2tm.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SendEmail {
    @Autowired
    private JavaMailSender sender;

    // @Async
    public String sendMail(String to, String subject, String message) {
        System.out.println("Sending email...");
       sendMailAsync(to, subject, message);
       System.out.println("Email sent");

        return "Email đã được gửi vui lòng đợi trong giây lát";
    }

    @Async("asyncExecutor")
    public void sendMailAsync(String to, String subject, String message) {
        Thread thread = new Thread(() -> {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(message);
            sender.send(msg);
        });
        thread.start();
    }
}
