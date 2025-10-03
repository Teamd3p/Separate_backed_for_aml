package com.tss.aml.controller;

import com.tss.aml.dto.AccountResponse;
import com.tss.aml.dto.CreateAccountRequest;
import com.tss.aml.entity.Account;
import com.tss.aml.entity.AuditAction;
import com.tss.aml.entity.AuditResourceType;
import com.tss.aml.service.AccountService;
import com.tss.aml.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AuditService auditService;

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<AccountResponse> createAccount(
            @PathVariable Long customerId,
            @Valid @RequestBody CreateAccountRequest request,
            HttpServletRequest httpRequest) {
        
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        try {
            Account createdAccount = accountService.createAccount(request, customerId);
            
            AccountResponse response = new AccountResponse();
            response.setAccountId(createdAccount.getAccountId());
            response.setAccountNumber(createdAccount.getAccountNumber());
            response.setAccountType(createdAccount.getAccountType() != null ? createdAccount.getAccountType().name() : null);
            response.setCurrency(createdAccount.getCurrency());
            response.setBalance(createdAccount.getBalance());
            response.setStatus(createdAccount.getStatus() != null ? createdAccount.getStatus().name() : null);
            response.setCreatedAt(createdAccount.getCreatedAt());
            response.setCustomerEmail(createdAccount.getCustomer() != null ? createdAccount.getCustomer().getEmail() : null);
            
            auditService.logSuccess(AuditAction.ACCOUNT_CREATED, AuditResourceType.ACCOUNT, 
                createdAccount.getAccountId(), null, null, 
                "Account created for customer: " + customerId, ipAddress);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            auditService.logFailure(AuditAction.ACCOUNT_CREATED, AuditResourceType.ACCOUNT, null, 
                null, null, "Account creation failed for customer " + customerId + ": " + e.getMessage(), ipAddress);
            throw e;
        }
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

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<AccountResponse> getAccountBalance(
            @PathVariable String accountNumber,
            HttpServletRequest httpRequest) {
        
        String ipAddress = getClientIpAddress(httpRequest);
        
        Account account = accountService.getAccountByNumber(accountNumber);
        if (account == null) {
            auditService.logFailure(AuditAction.DATA_VIEWED, AuditResourceType.ACCOUNT, null, 
                null, null, "Account balance query failed - account not found: " + accountNumber, ipAddress);
            return ResponseEntity.notFound().build();
        }
        
        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getAccountId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType() != null ? account.getAccountType().name() : null);
        response.setCurrency(account.getCurrency());
        response.setBalance(account.getBalance());
        response.setStatus(account.getStatus() != null ? account.getStatus().name() : null);
        response.setCreatedAt(account.getCreatedAt());
        response.setCustomerEmail(account.getCustomer() != null ? account.getCustomer().getEmail() : null);
        
        auditService.logSuccess(AuditAction.DATA_VIEWED, AuditResourceType.ACCOUNT, 
            account.getAccountId(), null, null, 
            "Account balance queried for: " + accountNumber, ipAddress);
        
        return ResponseEntity.ok(response);
    }
}
