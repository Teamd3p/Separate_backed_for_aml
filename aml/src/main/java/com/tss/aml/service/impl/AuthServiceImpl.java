package com.tss.aml.service.impl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.aml.dto.VerifyOtpRequest;
import com.tss.aml.dto.request.LoginRequest;
import com.tss.aml.dto.request.RegisterRequest;
import com.tss.aml.dto.response.AuthResponse;
import com.tss.aml.entity.Customer;
import com.tss.aml.entity.User;
import com.tss.aml.entity.enums.Role;
import com.tss.aml.entity.enums.UserStatus;
import com.tss.aml.exception.UserApiException;
import com.tss.aml.repository.CustomerRepository;
import com.tss.aml.repository.UserRepository;
import com.tss.aml.service.AuthService;
import com.tss.aml.service.EmailService;
import com.tss.aml.util.JwtUtil;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private static final int OTP_EXPIRY_MINUTES = 10;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserApiException("Email already registered");
        }

        // Create customer entity
        Customer customer = new Customer();
        customer.setEmail(request.getEmail());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setRole(Role.CUSTOMER);
        customer.setFirstName(request.getFirstName());
        customer.setMiddleName(request.getMiddleName());
        customer.setLastName(request.getLastName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setNationality(request.getNationality());
        customer.setContactNumber(request.getContactNumber());
        customer.setStreet(request.getStreet());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setNation(request.getNation());
        customer.setPincode(request.getPincode());
        customer.setStatus(UserStatus.PENDING_VERIFICATION);
        customer.setEmailVerified(false);

        // Generate OTP
        String otp = generateOtp();
        customer.setVerificationOtp(otp);
        customer.setOtpExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));

        // Save customer
        customerRepository.save(customer);

        // Send OTP email
        try {
            emailService.sendOtpEmail(customer.getEmail(), otp);
        } catch (Exception e) {
            throw new UserApiException("Failed to send OTP email. Please try again.");
        }

        return new AuthResponse("Registration successful! Please check your email for OTP verification.");
    }

    @Override
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserApiException("User not found"));

        // Check if already verified
        if (user.isEmailVerified()) {
            throw new UserApiException("Email already verified. Please login.");
        }

        // Check if OTP matches
        if (!request.getOtp().equals(user.getVerificationOtp())) {
            throw new UserApiException("Invalid OTP");
        }

        // Check if OTP expired
        if (user.getOtpExpiryTime() == null || LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
            throw new UserApiException("OTP has expired. Please request a new one.");
        }

        // Verify email
        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);
        user.setVerificationOtp(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);

        // Send welcome email
        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            try {
                emailService.sendWelcomeEmail(customer.getEmail(), customer.getFirstName());
            } catch (Exception e) {
                // Continue even if welcome email fails
            }
        }

        return new AuthResponse("Email verified successfully! You can now login.");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserApiException("Invalid email or password"));

        // Check if email is verified
        if (!user.isEmailVerified()) {
            throw new UserApiException("Email not verified. Please verify your email first.");
        }

        // Check if account is active
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserApiException("Account is not active. Please contact support.");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UserApiException("Invalid email or password");
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token, user.getEmail(), user.getRole().name(), "Login successful");
    }

    @Override
    public AuthResponse resendOtp(String email) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserApiException("User not found"));

        // Check if already verified
        if (user.isEmailVerified()) {
            throw new UserApiException("Email already verified. Please login.");
        }

        // Generate new OTP
        String otp = generateOtp();
        user.setVerificationOtp(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        userRepository.save(user);

        // Send OTP email
        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            throw new UserApiException("Failed to send OTP email. Please try again.");
        }

        return new AuthResponse("OTP has been resent to your email.");
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
