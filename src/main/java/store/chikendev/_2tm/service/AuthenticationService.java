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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.NonFinal;
import store.chikendev._2tm.dto.request.LoginRequest;
import store.chikendev._2tm.dto.request.LogoutRequest;
import store.chikendev._2tm.dto.request.RefreshTokenRequest;
import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.AuthenticationResponse;
import store.chikendev._2tm.dto.responce.CartResponse;
import store.chikendev._2tm.dto.responce.RoleResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.CartItems;
import store.chikendev._2tm.entity.InvaLidatedToken;
import store.chikendev._2tm.entity.Role;
import store.chikendev._2tm.entity.RoleAccount;
import store.chikendev._2tm.entity.StateAccount;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.InvaLidatedTokenRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;

@Service
public class AuthenticationService {

    public static final boolean REFRESH_TOKEN = true;
    public static final boolean ACCESS_TOKEN = false;

    public static final String LOGIN_ROLE_ADMIN = "ROLE_ADMIN";
    public static final String LOGIN_ROLE_STAFF = "ROLE_STAFF";
    public static final String LOGIN_ROLE_USER = "ROLE_USER";
    public static final String LOGIN_ROLE_DELIVERY = "ROLE_DELIVERY";
    @Autowired
    private AddressService addressService;

    @Autowired
    HttpServletRequest request;

    @Autowired
    private ProductService productService;

    @Autowired
    HttpServletResponse response;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InvaLidatedTokenRepository invaLidatedTokenRepository;

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

    // thoi gian con hieu luc de lam moi token
    @NonFinal
    @Value("${jwt.refreshable-duration}")
    private Long REFRESHABLE_DURATION;

    // đăng nhập
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AuthenticationResponse auth(LoginRequest request, String role_login) {
        Account user = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getState().getId() == StateAccount.LOCKED) {
            throw new AppException(ErrorCode.ACCOUNT_BLOCKED);
        }

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.LOGIN_FAIL);
        }

        List<RoleAccount> aRoles = user.getRoles();

        if (LOGIN_ROLE_ADMIN.equals(role_login)) {
            boolean hasRole = aRoles.stream().anyMatch(role -> role.getRole().getId().equals(Role.ROLE_ADMIN));
            if (!hasRole) {
                throw new AppException(ErrorCode.LOGIN_ROLE_REQUIRED);
            }
        }

        if (LOGIN_ROLE_DELIVERY.equals(role_login)) {
            boolean hasRole = aRoles.stream().anyMatch(role -> role.getRole().getId().equals(Role.ROLE_SHIPPER));
            if (!hasRole) {
                throw new AppException(ErrorCode.LOGIN_ROLE_REQUIRED);
            }
        }

        if (LOGIN_ROLE_STAFF.equals(role_login)) {
            boolean hasRole = aRoles.stream().anyMatch(role -> role.getRole().getId().equals(Role.ROLE_STAFF)
                    || role.getRole().getId().equals(Role.ROLE_STAFF));
            if (!hasRole) {
                throw new AppException(ErrorCode.LOGIN_ROLE_REQUIRED);
            }
        }

        if (LOGIN_ROLE_USER.equals(role_login)) {
            boolean hasRole = aRoles.stream().anyMatch(role -> role.getRole().getId().equals(Role.ROLE_USER)
                    || role.getRole().getId().equals(Role.ROLE_CUSTOMER)
                    || role.getRole().getId().equals(Role.ROLE_PRODUCT_OWNER));
            if (!hasRole) {
                throw new AppException(ErrorCode.LOGIN_ROLE_REQUIRED);
            }
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

        var token = this.generateToken(user, VALID_DURATION, SIGNER_KEY);
        var refreshToken = this.generateToken(user, REFRESHABLE_DURATION, SIGNER_KEY_REFRESH);

        var image = FilesHelp.getOneDocument(user.getId(), EntityFileType.USER_AVATAR);

        List<RoleResponse> roles = new ArrayList();
        user.getRoles().forEach(role -> {
            roles.add(RoleResponse.builder()
                    .id(role.getRole().getId())
                    .name(role.getRole().getName())
                    .build());
        });

        response.setHeader("accessToken", token);
        response.setHeader("refreshToken", refreshToken);
        response.setHeader("Authorization", token);
        
        List<CartItems> cartItems = user.getCartItems();

        List<CartResponse> cartResponses = null;
        if (!CollectionUtils.isEmpty(cartItems)) {
            cartResponses = cartItems.stream().map(cartItem -> {
                return CartResponse.builder()
                        .id(cartItem.getId())
                        .quantity(cartItem.getQuantity())
                        .product(productService.convertToResponse(cartItem.getProduct()))
                        .build();
            }).toList();
        }


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
                                .cartItems(cartResponses)
                                .primaryAddress(addressService.convertAddressToAddressResponse(user.getAddress()))
                                .build())
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    // tao token
    public String generateToken(Account account, Long duration, String signerKey) {
        // Tạo HMAC signer
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // Chuẩn bị JWT với tập hợp các khai báo
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(account.getEmail())
                .issuer("dnrea.demo")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(duration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", this.buildScope(account))
                .claim("fullname", account.getFullName())
                .claim("uid", account.getId())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        System.out.println("SIGNER_KEY: " + signerKey);
        // Ký token
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
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
    public void logout() throws ParseException, JOSEException {
        response.setHeader("Authorization", null);
        response.setHeader("accessToken", null);
        response.setHeader("refreshToken", null);

        String accessToken = getTokenFromRequest(request);
        String refreshToken = getRefreshTokenFromRequest(request);

        if (accessToken == null || refreshToken == null || "".equals(refreshToken) || "".equals(accessToken)) {
            return;
        }

        SignedJWT signAccessToken = verifyToken(accessToken, false);
        SignedJWT signRefreshToken = verifyToken(refreshToken, true);

        String jwtIdAccess = signAccessToken.getJWTClaimsSet().getJWTID();
        String jwtIdRefresh = signRefreshToken.getJWTClaimsSet().getJWTID();

        Date expiryTime = signAccessToken.getJWTClaimsSet().getExpirationTime();
        InvaLidatedToken invaLidatedAccessToken = InvaLidatedToken.builder()
                .id(jwtIdAccess)
                .expiryTime(expiryTime)
                .build();
        InvaLidatedToken invaLidatedRefreshToken = InvaLidatedToken.builder()
                .id(jwtIdRefresh)
                .expiryTime(expiryTime)
                .build();
        invaLidatedTokenRepository.save(invaLidatedAccessToken);
        invaLidatedTokenRepository.save(invaLidatedRefreshToken);

    }

    // kiem tra token
    public SignedJWT verifyToken(String token, boolean isRefresh) throws AppException {
        JWSVerifier verifier;
        SignedJWT signedJWT;
        var verified = false;
        Date expiryTime;

        try {
            signedJWT = SignedJWT.parse(token);
            System.out.println("SIGNER_KEY2: " + SIGNER_KEY);

            if (isRefresh) {
                verifier = new MACVerifier(SIGNER_KEY_REFRESH.getBytes());
            } else {
                verifier = new MACVerifier(SIGNER_KEY.getBytes());
            }
            verified = signedJWT.verify(verifier);
            if (isRefresh) {
                expiryTime = new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli());
            } else {
                expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            }

            if (invaLidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
                throw new AppException(ErrorCode.UNAUTHORIZED);
        } catch (JOSEException | ParseException e) {
            throw new RuntimeException(e);
        }

        if (signedJWT == null || verifier == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        } else {

            if (!verified)
                throw new AppException(ErrorCode.UNAUTHORIZED);

            if (expiryTime.before(new Date()))
                throw new AppException(ErrorCode.TOKEN_EXPIRED);

        }

        return signedJWT;
    }

    // lam moi token
    public AuthenticationResponse refreshToken(RefreshTokenRequest request)
            throws ParseException, JOSEException, AppException {
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

        var token = this.generateToken(account, VALID_DURATION, SIGNER_KEY);
        var refreshToken = this.generateToken(account, REFRESHABLE_DURATION, SIGNER_KEY_REFRESH);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    public boolean refreshToKenFromHttpServletRequest() {
        String refreshToken = getRefreshTokenFromRequest(request);

        if (refreshToken == null) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        SignedJWT signedJWT = verifyToken(refreshToken, AuthenticationService.REFRESH_TOKEN);

        if (signedJWT == null) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        } else {
            try {
                String userId = signedJWT.getJWTClaimsSet().getStringClaim("uid");
                Account account = accountRepository.findById(userId).get();

                if (account == null) {
                    throw new AppException(ErrorCode.TOKEN_INVALID);
                }

                String accessToken = generateToken(account, VALID_DURATION, SIGNER_KEY);
                String newRefreshToken = generateToken(account, REFRESHABLE_DURATION, SIGNER_KEY_REFRESH);

                response.setHeader("accessToken", accessToken);
                response.setHeader("refreshToken", newRefreshToken);
                response.setHeader("Authorization", accessToken);

                String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
                Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
                InvaLidatedToken invaLidatedToken = InvaLidatedToken.builder()
                        .id(jwtId)
                        .expiryTime(expiryTime)
                        .build();

                invaLidatedTokenRepository.save(invaLidatedToken);

                return true;
            } catch (ParseException e) {
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }
        }
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return token.substring(7);
    }

    public String getAccessTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("AccessToken");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return token.substring(7);
    }

    public String getRefreshTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("RefreshToken");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return token.substring(7);
    }

}
