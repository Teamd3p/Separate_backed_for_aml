package com.tss.aml.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tss.aml.entity.enums.TransactionStatus;
import com.tss.aml.entity.enums.TransactionType;

public class TransactionHistoryResponseDto {

    private Long transactionId;
    private String senderAccountNumber;
    private BigDecimal amount;
    private String currency;
    private TransactionType transactionType;
    private TransactionStatus status;
    private LocalDateTime timestamp;
    private String countryCode;

    // Constructors
    public TransactionHistoryResponseDto() {}

    public TransactionHistoryResponseDto(Long transactionId, String senderAccountNumber, 
                                        BigDecimal amount, 
                                        String currency, TransactionType transactionType, 
                                        TransactionStatus status, LocalDateTime timestamp, 
                                        String countryCode) {
        this.transactionId = transactionId;
        this.senderAccountNumber = senderAccountNumber;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.status = status;
        this.timestamp = timestamp;
        this.countryCode = countryCode;
    }

    // Getters and Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public String getSenderAccountNumber() { return senderAccountNumber; }
    public void setSenderAccountNumber(String senderAccountNumber) { this.senderAccountNumber = senderAccountNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
}
