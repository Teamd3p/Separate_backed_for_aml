package com.tss.aml.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.tss.aml.service.EmailService;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@aml.com}")
    private String fromEmail;
    
    // HTML Email Templates with inline CSS
    private static final String EMAIL_BASE_TEMPLATE = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>%s</title>
            <style>
                body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f5f5f5; }
                .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; }
                .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center; }
                .header h1 { color: #ffffff; margin: 0; font-size: 28px; font-weight: 300; }
                .content { padding: 40px 30px; }
                .otp-box { background-color: #f8f9fa; border: 2px dashed #667eea; border-radius: 10px; padding: 20px; text-align: center; margin: 20px 0; }
                .otp-code { font-size: 32px; font-weight: bold; color: #667eea; letter-spacing: 5px; font-family: 'Courier New', monospace; }
                .button { display: inline-block; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: #ffffff; padding: 12px 30px; text-decoration: none; border-radius: 25px; font-weight: 500; margin: 20px 0; }
                .footer { background-color: #2c3e50; color: #ecf0f1; padding: 20px; text-align: center; font-size: 14px; }
                .highlight { color: #667eea; font-weight: 600; }
                .warning { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 5px; }
                .success { background-color: #d4edda; border-left: 4px solid #28a745; padding: 15px; margin: 20px 0; border-radius: 5px; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>🛡️ AML System</h1>
                </div>
                <div class="content">
                    %s
                </div>
                <div class="footer">
                    <p>© 2024 AML System. All rights reserved.</p>
                    <p>This is an automated message. Please do not reply to this email.</p>
                </div>
            </div>
        </body>
        </html>
        """;
    
    private static final String OTP_EMAIL_CONTENT = """
        <h2>Email Verification Required</h2>
        <p>Dear User,</p>
        <p>Thank you for registering with our AML System. To complete your registration, please verify your email address using the OTP below:</p>
        
        <div class="otp-box">
            <p style="margin: 0; font-size: 16px; color: #666;">Your verification code is:</p>
            <div class="otp-code">%s</div>
        </div>
        
        <div class="warning">
            <strong>⚠️ Important:</strong> This OTP is valid for <strong>10 minutes only</strong>. Please use it promptly to verify your account.
        </div>
        
        <p>If you did not request this verification, please ignore this email or contact our support team.</p>
        
        <p>Best regards,<br>
        <span class="highlight">AML Security Team</span></p>
        """;
    
    private static final String WELCOME_EMAIL_CONTENT = """
        <h2>🎉 Welcome to AML System!</h2>
        <p>Dear <span class="highlight">%s</span>,</p>
        
        <div class="success">
            <strong>✅ Account Activated Successfully!</strong><br>
            Your email has been verified and your account is now fully active.
        </div>
        
        <p>You now have access to our comprehensive Anti-Money Laundering system with the following features:</p>
        
        <ul style="line-height: 1.8;">
            <li>🏦 <strong>Account Management</strong> - Create and manage multiple currency accounts</li>
            <li>💸 <strong>Secure Transactions</strong> - Transfer funds with real-time AML monitoring</li>
            <li>🔍 <strong>Transaction History</strong> - View detailed transaction records and status</li>
            <li>📊 <strong>Risk Assessment</strong> - Advanced fraud detection and compliance monitoring</li>
            <li>🌍 <strong>Multi-Currency Support</strong> - Handle transactions in various currencies</li>
        </ul>
        
        <div style="text-align: center; margin: 30px 0;">
            <a href="#" class="button">Access Your Dashboard</a>
        </div>
        
        <p>If you have any questions or need assistance, our support team is here to help.</p>
        
        <p>Welcome aboard!<br>
        <span class="highlight">The AML Team</span></p>
        """;
    
    private static final String ACCOUNT_CREATED_EMAIL_CONTENT = """
        <h2>🏦 New Account Created Successfully!</h2>
        <p>Dear <span class="highlight">%s</span>,</p>
        
        <div class="success">
            <strong>✅ Account Created!</strong><br>
            Your new %s account has been successfully created and is ready to use.
        </div>
        
        <div style="background-color: #f8f9fa; padding: 20px; border-radius: 10px; margin: 20px 0;">
            <h3 style="margin-top: 0; color: #495057;">📋 Account Details</h3>
            <table style="width: 100%%; border-collapse: collapse;">
                <tr style="border-bottom: 1px solid #dee2e6;">
                    <td style="padding: 10px 0; font-weight: 600;">Account Number:</td>
                    <td style="padding: 10px 0; font-family: 'Courier New', monospace; color: #667eea;">%s</td>
                </tr>
                <tr style="border-bottom: 1px solid #dee2e6;">
                    <td style="padding: 10px 0; font-weight: 600;">Currency:</td>
                    <td style="padding: 10px 0;">%s</td>
                </tr>
                <tr style="border-bottom: 1px solid #dee2e6;">
                    <td style="padding: 10px 0; font-weight: 600;">Account Type:</td>
                    <td style="padding: 10px 0;">%s</td>
                </tr>
                <tr>
                    <td style="padding: 10px 0; font-weight: 600;">Initial Balance:</td>
                    <td style="padding: 10px 0; color: #28a745; font-weight: 600;">%s %s</td>
                </tr>
            </table>
        </div>
        
        <div class="warning">
            <strong>🔒 Security Notice:</strong> Keep your account number confidential and never share it with unauthorized parties.
        </div>
        
        <p>You can now:</p>
        <ul style="line-height: 1.8;">
            <li>💰 Make deposits and withdrawals</li>
            <li>🔄 Transfer funds to other accounts</li>
            <li>📊 Monitor your transaction history</li>
            <li>🛡️ Benefit from our advanced AML protection</li>
        </ul>
        
        <div style="text-align: center; margin: 30px 0;">
            <a href="#" class="button">View Account Dashboard</a>
        </div>
        
        <p>Thank you for choosing our AML System for your financial needs.</p>
        
        <p>Best regards,<br>
        <span class="highlight">AML Account Services</span></p>
        """;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🛡️ AML - Email Verification Required");
            
            String content = String.format(OTP_EMAIL_CONTENT, otp);
            String htmlContent = String.format(EMAIL_BASE_TEMPLATE, "Email Verification", content);
            
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🎉 Welcome to AML System - Account Activated!");
            
            String content = String.format(WELCOME_EMAIL_CONTENT, firstName);
            String htmlContent = String.format(EMAIL_BASE_TEMPLATE, "Welcome to AML System", content);
            
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            // Don't throw exception for welcome email failure
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }

	@Override
	public void sendNotificationEmail(String email, String subject, String message) {
		try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("🛡️ AML - " + subject);
            
            // Simple notification template
            String content = String.format("""
                <h2>%s</h2>
                <div style="background-color: #f8f9fa; padding: 20px; border-radius: 10px; margin: 20px 0;">
                    %s
                </div>
                <p>If you have any questions, please contact our support team.</p>
                <p>Best regards,<br>
                <span class="highlight">AML System</span></p>
                """, subject, message);
            
            String htmlContent = String.format(EMAIL_BASE_TEMPLATE, subject, content);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("Failed to send notification email: " + e.getMessage());
        }
	}

    @Override
    public void sendAccountCreatedEmail(String toEmail, String firstName, String accountNumber, String currency, String accountType, String initialBalance) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🏦 New Account Created - " + accountNumber);
            
            String content = String.format(ACCOUNT_CREATED_EMAIL_CONTENT, 
                firstName, currency, accountNumber, currency, accountType, initialBalance, currency);
            String htmlContent = String.format(EMAIL_BASE_TEMPLATE, "Account Created Successfully", content);
            
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("Failed to send account created email: " + e.getMessage());
        }
    }
}
