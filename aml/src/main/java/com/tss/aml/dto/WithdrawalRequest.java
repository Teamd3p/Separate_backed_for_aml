package com.tss.aml.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class WithdrawalRequest {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    private String description;
    private String purposeOfWithdrawal; // e.g., "PERSONAL", "BUSINESS", "INVESTMENT"

    // Constructors
    public WithdrawalRequest() {}

    public WithdrawalRequest(String accountNumber, BigDecimal amount, String currency, 
                            String description, String purposeOfWithdrawal) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.purposeOfWithdrawal = purposeOfWithdrawal;
    }

    // Getters and Setters
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPurposeOfWithdrawal() { return purposeOfWithdrawal; }
    public void setPurposeOfWithdrawal(String purposeOfWithdrawal) { this.purposeOfWithdrawal = purposeOfWithdrawal; }
}
