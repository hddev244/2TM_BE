package store.chikendev._2tm.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.experimental.NonFinal;
import store.chikendev._2tm.dto.request.LoginRequest;
import store.chikendev._2tm.dto.request.LogoutRequest;
import store.chikendev._2tm.dto.request.RefreshTokenRequest;
import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.AuthenticationResponse;
import store.chikendev._2tm.dto.responce.RoleResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.InvaLidatedToken;
import store.chikendev._2tm.entity.StateAccount;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.InvaLidatedTokenRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;

@Service
public class AuthenticationService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InvaLidatedTokenRepository invaLidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    // thoi gian hieu luc cua token
    @NonFinal
    @Value("${jwt.valid-duration}")
    private Long VALID_DURATION;

    // thoi gian con hieu luc de lam moi token
    @NonFinal
    @Value("${jwt.refreshable-duration}")
    private Long REFRESHABLE_DURATION;

    // đăng nhập
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AuthenticationResponse auth(LoginRequest request) {
        Account user = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getState().getId() == StateAccount.LOCKED) {
            throw new AppException(ErrorCode.ACCOUNT_BLOCKED);
        }

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.LOGIN_FAIL);
        }

        if (user.getState().getId() == StateAccount.VERIFICATION_REQUIRED) {
            return AuthenticationResponse.builder()
                    .account(
                            AccountResponse.builder()
                                    .email(user.getEmail())
                                    .build())
                    .authenticated(false)
                    .build();
        }

        var token = this.generateToken(user);

        var image = FilesHelp.getOneDocument(user.getId(), EntityFileType.USER_AVATAR);

        List<RoleResponse> roles = new ArrayList();
        user.getRoles().forEach(role -> {
            roles.add(RoleResponse.builder()
                    .id(role.getRole().getId())
                    .name(role.getRole().getName())
                    .build());
        });

        return AuthenticationResponse.builder()
                .authenticated(authenticated)
                .account(
                        AccountResponse.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .fullName(user.getFullName())
                                .roles(roles)
                                .violationPoints(user.getViolationPoints())
                                .phoneNumber(user.getPhoneNumber())
                                .email(user.getEmail())
                                .image(image)
                                .build())
                .token(token)
                .build();
    }

    // tao token
    private String generateToken(Account account) {
        // Tạo HMAC signer
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // Chuẩn bị JWT với tập hợp các khai báo
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(account.getEmail())
                .issuer("dnrea.demo")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", this.buildScope(account))
                .claim("fullname", account.getFullName())
                .build();

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

    // set cac roles
    private String buildScope(Account account) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(account.getRoles())) {
            account.getRoles().forEach(s -> stringJoiner.add(s.getRole().getId()));
        }
        return stringJoiner.toString();
    }

    // đăng xuất
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            SignedJWT signToken = verifyToken(request.getToken(), true);

            String jwtId = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvaLidatedToken invaLidatedToken = InvaLidatedToken.builder()
                    .id(jwtId)
                    .expiryTime(expiryTime)
                    .build();
            invaLidatedTokenRepository.save(invaLidatedToken);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNAUTHORIZED);// TODO: handle exception
        }
    }

    // kiem tra token
    public SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime;

        if (isRefresh) {
            expiryTime = new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
                    .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli());
        } else {
            expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        }
        var verified = signedJWT.verify(verifier);

        if (!verified)
            throw new AppException(ErrorCode.UNAUTHORIZED);
        if (expiryTime.before(new Date()))
            throw new AppException(ErrorCode.UNAUTHORIZED);
        if (invaLidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        return signedJWT;
    }

    // lam moi token
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        SignedJWT verify = this.verifyToken(request.getToken(), true);
        String id = verify.getJWTClaimsSet().getJWTID();
        Date expiryTime = verify.getJWTClaimsSet().getExpirationTime();

        InvaLidatedToken invaLidatedToken = InvaLidatedToken.builder()
                .id(id)
                .expiryTime(expiryTime)
                .build();
        invaLidatedTokenRepository.save(invaLidatedToken);

        String email = verify.getJWTClaimsSet().getSubject();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var token = this.generateToken(account);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .build();

    }

}
