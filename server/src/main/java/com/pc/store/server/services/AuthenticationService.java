package com.pc.store.server.services;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.pc.store.server.dao.CustomerRespository;
import com.pc.store.server.dao.InvalidatedTokenRespository;
import com.pc.store.server.dto.request.IntrospectRequest;
import com.pc.store.server.dto.request.LogoutRequest;
import com.pc.store.server.dto.request.RefreshRequest;
import com.pc.store.server.dto.response.AuthenticationResponse;
import com.pc.store.server.dto.response.IntrospectResponse;
import com.pc.store.server.entities.Customer;
import com.pc.store.server.entities.InvalidatedToken;
import com.pc.store.server.exception.AppException;
import com.pc.store.server.exception.ErrorCode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    CustomerRespository customerRespository;

    InvalidatedTokenRespository invalidatedTokenRespository;

    @NonFinal
    @Value("${jwt.expireDuration}")
    protected long EXPIRE_DURATION;

    @NonFinal
    @Value("${jwt.refeshableDuration}")
    protected long REFRESHABLE_DURATION;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    public AuthenticationResponse authenticate(String userName, String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var customer = customerRespository
                .findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        boolean isAuthenticated = passwordEncoder.matches(password, customer.getPassword());
        if (!isAuthenticated) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        var token = generateToken(customer);
        return AuthenticationResponse.builder()
                .isAuthenticated(true)
                .token(token)
                .build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryDate = (isRefresh)
                ? new Date(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean verified = signedJWT.verify(verifier);
        if (!(verified && expiryDate.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);
        if (invalidatedTokenRespository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        return signedJWT;
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    public String generateToken(Customer customer) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(customer.getUserName())
                .issuer("pc-store")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(EXPIRE_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token");
            throw new RuntimeException(e);
        }
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), false);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryDate(expiryTime).build();
            invalidatedTokenRespository.save(invalidatedToken);
        } catch (AppException e) {
            log.info("Token is invalid");
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryDate(expiryTime).build();
        invalidatedTokenRespository.save(invalidatedToken);
        var userName = signedJWT.getJWTClaimsSet().getSubject();
        var user = customerRespository
                .findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_EXISTED));
        return AuthenticationResponse.builder()
                .token(generateToken(user))
                .isAuthenticated(true)
                .build();
    }
}
