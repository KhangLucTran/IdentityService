package com.example.SpringNe.service;

import com.example.SpringNe.dto.request.AuthenticationRequest;
import com.example.SpringNe.dto.request.IntrospectRequest;
import com.example.SpringNe.dto.request.LogoutRequest;
import com.example.SpringNe.dto.request.RefreshRequest;
import com.example.SpringNe.dto.response.AuthenticationResponse;
import com.example.SpringNe.dto.response.IntrospectResponse;
import com.example.SpringNe.entity.InvalidatedToken;
import com.example.SpringNe.entity.User;
import com.example.SpringNe.exception.AppException;
import com.example.SpringNe.exception.ErrorCode;
import com.example.SpringNe.repository.InvalidatedTokenRepository;
import com.example.SpringNe.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.StringJoiner;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    @Autowired
    UserRepository userRepository;

    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.secret}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    //verify token
    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }


    // Login vs Authenticate: check if the user exists.
    // If the user exists, return true;
    // Else return false;
    // After checking if the user exists, Then generate a token
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean auth = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!auth)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }


    // Generate Token
    private String generateToken(User user) {
        // để tạo 1 Token bằng JWS cần 2 phần: Header và Payload
        /// Phần Header: chứa thuật toán sử dụng để  GeneratedToken
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        // Phần Payload: chứa jwtClaimsSet
        // JWTClaimsSet: chứa subject, issuer(Người tạo token), issueTime(Time tạo ra Token),
        // expirationTime(Time tồn tại của Token)
        // JWTID : id của token mới tạo
        // Scope: định nghĩa ROLE_
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("khangluctran.com")  // xác định token được issuer từ ai: Khangluctran
                .issueTime(new Date()) // Thời gian issue token
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                )) //Xác định thời gian tồn tại của Token: Hết hạn sau 1 tiếng đồng hồ
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        // 1 object token  gồm 2 phần: header và payload
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        // Sau khi tạo, kí token; dùng hàm sign để kí
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    // Build Scope to GenerateToken
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(
                    role -> {
                        stringJoiner.add("ROLE_" + role.getName());
                        if (!CollectionUtils.isEmpty(role.getPermisstions()))
                            role.getPermisstions().forEach(permisstion -> stringJoiner.add(permisstion.getName()));
                    }
            );
        }
        return stringJoiner.toString();
    }


    //Logout
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }


    // Refresh Token
    public AuthenticationResponse refreshToken(RefreshRequest request)
            throws ParseException, JOSEException {
        // Check the time of the Token
        var signJWT = verifyToken(request.getToken(), true);

        //Refresh Token
        // Get the ID of the token
        var jit = signJWT.getJWTClaimsSet().getJWTID();
        // Get the Time of the token
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        // Logout token
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
        // Find a username to build a Token
        var username = signJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.UNAUTHENTICATED)
        );
        // Build token based on user information.
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }


    // VerifyToken
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);


        // If isRefresh is true, the expiryTime is the time of RefreshToken
        // Else the expiryTime is the time of Authenticated
        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(jwsVerifier);
        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }
}
