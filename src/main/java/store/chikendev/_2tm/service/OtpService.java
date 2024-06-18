package store.chikendev._2tm.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.OtpRequest;
import store.chikendev._2tm.dto.responce.OtpResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.Otp;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.OtpRepository;
import store.chikendev._2tm.repository.StateAccountRepository;
import store.chikendev._2tm.utils.SendEmail;
import store.chikendev._2tm.utils.SendOtp;

@Service
public class OtpService {

    @Autowired
    private SendOtp otpPhone;

    @Autowired
    private SendEmail otpEmail;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private StateAccountRepository stateAccountRepository;

    @Autowired
    private AccountRepository accountRepository;

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String VIETNAM_PHONE_REGEX = "^0\\d{9}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(VIETNAM_PHONE_REGEX);

    public OtpResponse sendOtp(String input) {
        if (EMAIL_PATTERN.matcher(input).matches()) {
            String otp = generateOtp();
            Account account = accountRepository.findByEmail(input).orElseThrow(() -> {
                throw new RuntimeException("Email sai");
            });
            Otp save = create(account, otp);
            String subject = "2TM Xin chào bạn";
            String message = "Mã OTP của bạn là: " + save.getTokenCode();
            return OtpResponse.builder()
                    .success(otpEmail.sendMail(input, subject, message))
                    .input(input)
                    .build();
        } else if (PHONE_PATTERN.matcher(input).matches()) {
            String otp = generateOtp();
            String phoneNumber = input;

            Account account = accountRepository.findByPhoneNumber(input).orElseThrow(() -> {
                throw new RuntimeException("Số điện thoại sai");
            });
            Otp save = create(account, otp);
            if (phoneNumber.startsWith("0")) {
                phoneNumber = "84" + input.substring(1);
            }
            return OtpResponse.builder()
                    .success(otpPhone.sendOtp(phoneNumber, save.getTokenCode()))
                    .input(input)
                    .build();
        } else {
            throw new RuntimeException("Vui lòng kiểm tra lại email hoặc SDT");
        }
    }

    public String validateOtp(OtpRequest otp) {
        if (EMAIL_PATTERN.matcher(otp.getInput()).matches()) {
            Account account = accountRepository.findByEmail(otp.getInput()).orElseThrow(() -> {
                throw new RuntimeException("Email sai");
            });
            List<Otp> otps = otpRepository.findByAccount(account);
            if (otps.size() == 0) {
                throw new RuntimeException("Mã OTP không tồn tại");
            }
            if (otps.get(0).getTokenCode().equals(otp.getOtp())) {
                boolean validateDate = isWithinFiveMinutes(otps.get(0).getCreatedAt());
                if (validateDate) {
                    account.setState(stateAccountRepository.findById(Long.valueOf(1)).get());
                    accountRepository.saveAndFlush(account);
                    otpRepository.delete(otps.get(0));
                    return "Xác thực thành công";
                }
                return "Mã OTP đã hết hạn";

            } else {
                throw new RuntimeException("Mã OTP không đúng");
            }
        } else if (PHONE_PATTERN.matcher(otp.getInput()).matches()) {
            Account account = accountRepository.findByPhoneNumber(otp.getInput()).orElseThrow(() -> {
                throw new RuntimeException("Số điện thoại sai");
            });
            List<Otp> otps = otpRepository.findByAccount(account);
            if (otps.size() == 0) {
                throw new RuntimeException("Mã OTP không tồn tại");
            }
            if (otps.get(0).getTokenCode().equals(otp.getOtp())) {
                boolean validateDate = isWithinFiveMinutes(otps.get(0).getCreatedAt());
                if (validateDate) {
                    account.setState(stateAccountRepository.findById(Long.valueOf(1)).get());
                    accountRepository.save(account);
                    otpRepository.delete(otps.get(0));
                    return "Xác thực thành công";
                }
                return "Mã OTP đã hết hạn";
            } else {
                throw new RuntimeException("Mã OTP không đúng");
            }
        } else {
            throw new RuntimeException("Vui lòng kiểm tra lại email hoặc SDT");
        }

    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public Otp create(Account account, String otp) {
        if (account.getState().getId() != 4) {
            throw new RuntimeException("Tài khoản đã được kích hoạt");
        }
        List<Otp> validate = otpRepository.findByAccount(account);
        if (validate.size() > 0) {
            otpRepository.delete(validate.get(0));
        }
        Otp save = Otp.builder()
                .tokenCode(otp)
                .account(account)
                .build();
        return otpRepository.saveAndFlush(save);
    }

    // kiểm tra xem mã OTP có quá 5 phút từ lúc tạo hay không
    private boolean isWithinFiveMinutes(Date dateToCheck) {
        LocalDateTime now = LocalDateTime.now(); // Thời gian hiện tại
        LocalDateTime dateToCheckLocal = convertToLocalDateTimeViaInstant(dateToCheck); // Chuyển đổi Date sang
                                                                                        // LocalDateTime
        Duration duration = Duration.between(dateToCheckLocal, now);
        long diffInMinutes = Math.abs(duration.toMinutes());
        System.out.println(diffInMinutes);
        return diffInMinutes <= 5;
    }

    private LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
