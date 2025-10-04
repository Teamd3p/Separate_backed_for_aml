package com.tss.aml.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tss.aml.entity.enums.TransactionStatus;
import com.tss.aml.entity.enums.TransactionType;

public class TransactionResponseDto {

    private Long transactionId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private TransactionType transactionType;
    private TransactionStatus status;
    private LocalDateTime timestamp;
    private String countryCode;
    private Integer riskScore;

    // Constructors
    public TransactionResponseDto() {}

    public TransactionResponseDto(Long transactionId, BigDecimal amount, String currency, 
                                 String description, TransactionType transactionType, 
                                 TransactionStatus status, LocalDateTime timestamp, 
                                 String countryCode, Integer riskScore) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.transactionType = transactionType;
        this.status = status;
        this.timestamp = timestamp;
        this.countryCode = countryCode;
        this.riskScore = riskScore;
    }

    // Getters and Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
}
