package com.pc.store.server.controllers;

import java.text.ParseException;

import org.springframework.web.bind.annotation.*;

import com.nimbusds.jose.JOSEException;
import com.pc.store.server.dto.request.*;
import com.pc.store.server.dto.response.AuthenticationResponse;
import com.pc.store.server.dto.response.IntrospectResponse;
import com.pc.store.server.services.AuthenticationService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/log-in")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.authenticate(request.getUserName(), request.getPassword()))
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.refreshToken(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }
}
