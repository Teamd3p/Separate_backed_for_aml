package com.tss.aml.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DepositRequestDto {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Country code is required")
    private String countryCode;

    @NotBlank(message = "Source of funds is required")
    private String sourceOfFunds; // e.g., "SALARY", "BUSINESS", "INVESTMENT"

    private String description;

    // Constructors
    public DepositRequestDto() {}

    public DepositRequestDto(String accountNumber, BigDecimal amount, String currency, 
                            String countryCode, String sourceOfFunds, String description) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.countryCode = countryCode;
        this.sourceOfFunds = sourceOfFunds;
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

    public String getSourceOfFunds() { return sourceOfFunds; }
    public void setSourceOfFunds(String sourceOfFunds) { this.sourceOfFunds = sourceOfFunds; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
