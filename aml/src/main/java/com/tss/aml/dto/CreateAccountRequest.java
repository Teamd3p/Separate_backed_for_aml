package com.tss.aml.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public class CreateAccountRequest {

    @NotNull(message = "Account type is required")
    private String accountType; // CURRENT, SAVING, SALARY

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3-letter ISO code (e.g., USD, INR)")
    private String currency;

    @NotNull(message = "Balance is required")
    @PositiveOrZero(message = "Balance must be zero or positive")
    private BigDecimal balance;

    // Constructors
    public CreateAccountRequest() {}

    public CreateAccountRequest(String accountType, String currency, BigDecimal balance) {
        this.accountType = accountType;
        this.currency = currency;
        this.balance = balance;
    }

    // Getters and Setters
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
