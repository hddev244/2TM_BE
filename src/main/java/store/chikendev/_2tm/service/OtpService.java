package store.chikendev._2tm.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.experimental.NonFinal;
import store.chikendev._2tm.dto.request.ForgotPasswordRequest;
import store.chikendev._2tm.dto.request.OtpRequest;
import store.chikendev._2tm.dto.responce.OtpResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.InvaLidatedToken;
import store.chikendev._2tm.entity.Otp;
import store.chikendev._2tm.entity.StateAccount;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.InvaLidatedTokenRepository;
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

    @Autowired
    private InvaLidatedTokenRepository invaLidatedTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.signerKeyRefresh}")
    private String SIGNER_KEY_REFRESH;

    // thoi gian hieu luc cua token
    @NonFinal
    @Value("${jwt.valid-duration}")
    private Long VALID_DURATION;

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

    // QMK - kiểm tra email xem có tồn tại và gửi mã OTP
    public String checkEmail(String email) {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        });
        if (account.getState().getId() == StateAccount.LOCKED) {
            throw new AppException(ErrorCode.ACCOUNT_BLOCKED);
        }
        String otp = generateOtp();
        List<Otp> otpDatabase = otpRepository.findByAccount(account);
        if (otpDatabase.size() > 0) {
            otpRepository.deleteAllInBatch(otpDatabase);
        }
        otpRepository.saveAndFlush(Otp.builder()
                .account(account)
                .tokenCode(otp) //OTP code
                .build());
        String emailContent = "<html>"
                + "<body>"
                + "<h3>Xin chào,</h3>"
                + "<p>Bạn đã yêu cầu lấy lại mật khẩu của mình. Vui lòng sử dụng mã OTP sau để tiếp tục quá trình đặt lại mật khẩu:</p>"
                + "<h2 style='color:blue;'>" + otp + "</h2>"
                + "<p>Mã này sẽ hết hạn sau 5 phút. Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>"
                + "<br>"
                + "<p>Trân trọng,</p>"
                + "<p>Đội ngũ hỗ trợ của 2TM</p>"
                + "</body>"
                + "</html>";
        otpEmail.sendMail(email, "QUÊN MẬT KHẨU TÀI KHOẢN 2TM", emailContent);
        return account.getEmail();
    }

    public String checkOtp(String email, String otp) {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        });
        Otp checkOtp = otpRepository.findById(otp).orElseThrow(() -> {
            throw new AppException(ErrorCode.OTP_INVALID);
        });

        if (!isWithinFiveMinutes(checkOtp.getCreatedAt())) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }
        if (!checkOtp.getAccount().getId().equals(account.getId())) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }
        return generateToken(account, otp);
    }

    public String changePassword(ForgotPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getReNewPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        String otp;
        String email;
        String id;
        try {
            // Giải mã token
            SignedJWT signedJWT = SignedJWT.parse(request.getToken());
            // Xác thực token với khóa ký
            boolean isVerified = signedJWT.verify(new MACVerifier(SIGNER_KEY.getBytes()));
            if (isVerified) {
                // Đọc các claims từ token
                JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
                if (claimsSet.getSubject() != null) {
                    email = claimsSet.getSubject();
                } else {
                    throw new AppException(ErrorCode.TOKEN_INVALID);
                }
                if (claimsSet.getStringClaim("otp") != null) {
                    otp = claimsSet.getStringClaim("otp");
                } else {
                    throw new AppException(ErrorCode.TOKEN_INVALID);
                }
                if (claimsSet.getJWTID() != null) {
                    id = claimsSet.getJWTID();
                } else {
                    throw new AppException(ErrorCode.TOKEN_INVALID);
                }
            } else {
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        Otp checkOtp = otpRepository.findById(otp).orElseThrow(() -> {
            throw new AppException(ErrorCode.OTP_INVALID);
        });
        if (!checkOtp.getAccount().getEmail().equals(email)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        InvaLidatedToken checkToken = invaLidatedTokenRepository.findById(id).orElseThrow(() -> {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        });
        if (!isWithinFiveMinutes(checkToken.getExpiryTime())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        Account account = checkOtp.getAccount();
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.saveAndFlush(account);
        return "Đổi mật khẩu thành công";
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
        return diffInMinutes <= 5;
    }

    private LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    // tao token QMK
    public String generateToken(Account account, String otp) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        // Chuẩn bị JWT với tập hợp các khai báo
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(account.getEmail())
                .issuer("2tm.demo")
                .jwtID(UUID.randomUUID().toString())
                .claim("otp", otp)
                .build();
        invaLidatedTokenRepository.saveAndFlush(InvaLidatedToken.builder()
                .id(claimsSet.getJWTID())
                .expiryTime(new Date())
                .build());
        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        // Ký token
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

    }
}
