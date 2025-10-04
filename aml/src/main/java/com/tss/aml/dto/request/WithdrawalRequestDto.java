package com.tss.aml.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class WithdrawalRequestDto {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Country code is required")
    private String countryCode;

    @NotBlank(message = "Purpose of withdrawal is required")
    private String purposeOfWithdrawal; // e.g., "PERSONAL", "BUSINESS", "INVESTMENT"

    private String description;

    // Constructors
    public WithdrawalRequestDto() {}

    public WithdrawalRequestDto(String accountNumber, BigDecimal amount, String currency, 
                               String countryCode, String purposeOfWithdrawal, String description) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.countryCode = countryCode;
        this.purposeOfWithdrawal = purposeOfWithdrawal;
        this.description = description;
    }

    // Getters and Setters
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getPurposeOfWithdrawal() { return purposeOfWithdrawal; }
    public void setPurposeOfWithdrawal(String purposeOfWithdrawal) { this.purposeOfWithdrawal = purposeOfWithdrawal; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
