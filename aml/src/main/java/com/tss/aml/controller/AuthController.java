package com.tss.aml.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tss.aml.dto.request.LoginRequest;
import com.tss.aml.dto.request.RegisterRequest;
import com.tss.aml.dto.request.VerifyOtpRequest;
import com.tss.aml.dto.response.AuthResponse;
import com.tss.aml.entity.enums.AuditAction;
import com.tss.aml.entity.enums.AuditResourceType;
import com.tss.aml.service.AuditService;
import com.tss.aml.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private AuditService auditService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);
        try {
            AuthResponse response = authService.register(request);
            auditService.logSuccess(AuditAction.REGISTER, AuditResourceType.USER, null, 
                null, request.getEmail(), "User registration successful", ipAddress);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            auditService.logFailure(AuditAction.REGISTER, AuditResourceType.USER, null, 
                null, request.getEmail(), "User registration failed: " + e.getMessage(), ipAddress);
            throw e;
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        AuthResponse response = authService.VerifyOtpRequest(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);
        try {
            AuthResponse response = authService.login(request);
            auditService.logSuccess(AuditAction.LOGIN, AuditResourceType.USER, null, 
                null, request.getEmail(), "User login successful", ipAddress);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            auditService.logFailure(AuditAction.LOGIN, AuditResourceType.USER, null, 
                null, request.getEmail(), "User login failed: " + e.getMessage(), ipAddress);
            throw e;
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<AuthResponse> resendOtp(@RequestParam String email, HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);
        
        AuthResponse response = authService.resendOtp(email);
        auditService.logSuccess(AuditAction.LOGIN, AuditResourceType.USER, null, 
            null, email, "OTP resend requested", ipAddress);
        return ResponseEntity.ok(response);
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
