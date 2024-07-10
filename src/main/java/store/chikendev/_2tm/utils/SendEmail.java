package store.chikendev._2tm.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

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
            try {
                MimeMessage messages = sender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(messages, true, "UTF-8");

                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(message, true);

                sender.send(messages);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
