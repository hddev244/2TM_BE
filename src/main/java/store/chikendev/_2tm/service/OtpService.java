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
import store.chikendev._2tm.entity.StateAccount;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.OtpRepository;
import store.chikendev._2tm.repository.StateAccountRepository;
import store.chikendev._2tm.utils.SendEmail;

@Service
public class OtpService {

    @Autowired
    private SendEmail otpEmail;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private StateAccountRepository stateAccountRepository;

    @Autowired
    private AccountRepository accountRepository;

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public OtpResponse sendOtp(String input) {
        if (EMAIL_PATTERN.matcher(input).matches()) {
            String otp = generateOtp();
            Account account = accountRepository.findByEmail(input).orElseThrow(() -> {
                throw new AppException(ErrorCode.EMAIL_PHONE_EXISTED);
            });
            List<Otp> otps = otpRepository.findByAccount(account);
            if (otps.size() > 0) {
                otpRepository.deleteAllInBatch(otps);
            }
            Otp save = create(account, otp);
            String subject = "2TM Xin chào bạn";
            String message = "Mã OTP của bạn là: " + save.getTokenCode();
            return OtpResponse.builder()
                    .success(otpEmail.sendMail(input, subject, message))
                    .input(input)
                    .build();
        } else {
            throw new RuntimeException("Vui lòng kiểm tra lại email hoặc SDT");
        }
    }

    public String validateOtp(OtpRequest otp) {
        if (EMAIL_PATTERN.matcher(otp.getInput()).matches()) {
            Account account = accountRepository.findByEmail(otp.getInput()).orElseThrow(() -> {
                throw new AppException(ErrorCode.OTP_INFO_INVALID);
            });

            if (account.getState().getId() == StateAccount.LOCKED) {
                throw new AppException(ErrorCode.ACCOUNT_BLOCKED);
            }

            if (account.getState().getId() != StateAccount.VERIFICATION_REQUIRED) {
                throw new AppException(ErrorCode.OTP_ACCOUNT_VERIFIED);
            }

            List<Otp> otps = otpRepository.findByAccount(account);
            if (otps.size() == 0) {
                throw new AppException(ErrorCode.OTP_REQUEST_NOT_FOUND);
            }
            if (otps.get(0).getTokenCode().equals(otp.getOtp())) {
                boolean validateDate = isWithinFiveMinutes(otps.get(0).getCreatedAt());
                if (validateDate) {
                    otpRepository.deleteAllInBatch(otps);
                    account.setState(stateAccountRepository.findById(StateAccount.ACTIVE).get());
                    accountRepository.saveAndFlush(account);
                    return "Xác thực thành công";
                }
                throw new AppException(ErrorCode.OTP_EXPIRED);
            } else {
                throw new AppException(ErrorCode.OTP_INVALID);
            }
        } else {
            throw new AppException(ErrorCode.OTP_INFO_INVALID);
        }

    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public Otp create(Account account, String otp) {
        if (account.getState().getId() != 4) {
            throw new AppException(ErrorCode.OTP_ACCOUNT_VERIFIED);
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
