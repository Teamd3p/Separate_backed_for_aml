package com.tss.aml.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tss.aml.entity.enums.TransactionStatus;
import com.tss.aml.entity.enums.TransactionType;

public class TransactionResponse {
    
    private Long transactionId;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private String currency;
    private String description;
    private TransactionType transactionType;
    private TransactionStatus status;
    private LocalDateTime timestamp;
    private String counterpartyName;
    private String counterpartyAccount;
    private String countryCode;
    private Integer riskScore;

    // Constructors
    public TransactionResponse() {}

    public TransactionResponse(Long transactionId, String senderAccountNumber, String receiverAccountNumber,
                              BigDecimal amount, String currency, String description, TransactionType transactionType,
                              TransactionStatus status, LocalDateTime timestamp, String counterpartyName,
                              String counterpartyAccount, String countryCode, Integer riskScore) {
        this.transactionId = transactionId;
        this.senderAccountNumber = senderAccountNumber;
        this.receiverAccountNumber = receiverAccountNumber;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.transactionType = transactionType;
        this.status = status;
        this.timestamp = timestamp;
        this.counterpartyName = counterpartyName;
        this.counterpartyAccount = counterpartyAccount;
        this.countryCode = countryCode;
        this.riskScore = riskScore;
    }

    // Getters and Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public String getSenderAccountNumber() { return senderAccountNumber; }
    public void setSenderAccountNumber(String senderAccountNumber) { this.senderAccountNumber = senderAccountNumber; }

    public String getReceiverAccountNumber() { return receiverAccountNumber; }
    public void setReceiverAccountNumber(String receiverAccountNumber) { this.receiverAccountNumber = receiverAccountNumber; }

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

    public String getCounterpartyName() { return counterpartyName; }
    public void setCounterpartyName(String counterpartyName) { this.counterpartyName = counterpartyName; }

    public String getCounterpartyAccount() { return counterpartyAccount; }
    public void setCounterpartyAccount(String counterpartyAccount) { this.counterpartyAccount = counterpartyAccount; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
}
