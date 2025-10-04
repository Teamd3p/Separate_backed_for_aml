package com.tss.aml.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tss.aml.entity.enums.TransactionStatus;
import com.tss.aml.entity.enums.TransactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "transactions", indexes = {
	    @Index(name = "idx_transactions_customer_status", columnList = "customer_id,status"),
	    @Index(name = "idx_transactions_country", columnList = "countryCode"),
	    @Index(name = "idx_transactions_timestamp", columnList = "timestamp DESC") // optional: for time-range queries
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "passwordHash", "verificationOtp", "otpExpiryTime"})
    private Customer customer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_account_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Account senderAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_account_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Account receiverAccount;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String currency;

    private LocalDateTime timestamp = LocalDateTime.now();
    private String description;
    private String counterpartyName;
    private String counterpartyAccount;
    private String countryCode;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TransactionStatus status = TransactionStatus.PENDING;

    public Transaction() {}

    public Transaction(Customer customer, Account senderAccount, Account receiverAccount, 
                       BigDecimal amount, String currency, String description, TransactionType type) {
        this.customer = customer;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.transactionType = type;
    }

    public Long getTransactionId() { return transactionId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCounterpartyName() { return counterpartyName; }
    public void setCounterpartyName(String counterpartyName) { this.counterpartyName = counterpartyName; }
    public String getCounterpartyAccount() { return counterpartyAccount; }
    public void setCounterpartyAccount(String counterpartyAccount) { this.counterpartyAccount = counterpartyAccount; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public Account getSenderAccount() { return senderAccount; }
    public void setSenderAccount(Account senderAccount) { this.senderAccount = senderAccount; }
    public Account getReceiverAccount() { return receiverAccount; }
    public void setReceiverAccount(Account receiverAccount) { this.receiverAccount = receiverAccount; }
}