package com.tss.aml.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
    void sendWelcomeEmail(String toEmail, String firstName);
    void sendNotificationEmail(String email, String subject, String message);
    void sendAccountCreatedEmail(String toEmail, String firstName, String accountNumber, String currency, String accountType, String initialBalance);
}
