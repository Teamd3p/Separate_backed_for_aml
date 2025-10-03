package com.tss.aml.service;

import com.tss.aml.dto.AuthResponse;
import com.tss.aml.dto.LoginRequest;
import com.tss.aml.dto.RegisterRequest;
import com.tss.aml.dto.VerifyOtpRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse verifyOtp(VerifyOtpRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse resendOtp(String email);
}
