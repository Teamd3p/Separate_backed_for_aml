package com.tss.aml.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.tss.aml.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@aml.com}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("AML - Email Verification OTP");
            message.setText("Dear User,\n\n" +
                    "Your OTP for email verification is: " + otp + "\n\n" +
                    "This OTP is valid for 10 minutes.\n\n" +
                    "If you did not request this, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "AML Team");

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to AML System");
            message.setText("Dear " + firstName + ",\n\n" +
                    "Welcome to the AML (Anti-Money Laundering) System!\n\n" +
                    "Your email has been successfully verified and your account is now active.\n\n" +
                    "You can now log in and access all features.\n\n" +
                    "Best regards,\n" +
                    "AML Team");

            mailSender.send(message);
        } catch (Exception e) {
            // Don't throw exception for welcome email failure
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }
}
