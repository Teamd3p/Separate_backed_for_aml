package com.tss.aml.service;

import com.tss.aml.dto.VerifyOtpRequest;
import com.tss.aml.dto.request.LoginRequest;
import com.tss.aml.dto.request.RegisterRequest;
import com.tss.aml.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse verifyOtp(VerifyOtpRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse resendOtp(String email);
}
