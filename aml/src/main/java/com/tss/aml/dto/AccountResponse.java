package com.tss.aml.dto;

import java.time.LocalDateTime;

public class AccountResponse {
    private Long accountId;
    private String accountNumber;
    private String accountType;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
    private String customerEmail;

    // Constructors
    public AccountResponse() {}

    public AccountResponse(Long accountId, String accountNumber, String accountType, 
                          String currency, String status, LocalDateTime createdAt, String customerEmail) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
        this.customerEmail = customerEmail;
    }

    // Getters and Setters
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
}
