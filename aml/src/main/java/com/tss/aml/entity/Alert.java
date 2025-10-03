package com.tss.aml.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @NotNull
    private String ruleTriggered;

    @NotNull
    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    @NotNull
    private AlertStatus status = AlertStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private ComplianceOfficer assignedTo;

    public Alert() {}

    public Alert(Customer customer, String ruleTriggered, int riskScore) {
        this.customer = customer;
        this.ruleTriggered = ruleTriggered;
        this.riskScore = riskScore;
    }

    public Long getAlertId() { return alertId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    public String getRuleTriggered() { return ruleTriggered; }
    public void setRuleTriggered(String ruleTriggered) { this.ruleTriggered = ruleTriggered; }
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    public AlertStatus getStatus() { return status; }
    public void setStatus(AlertStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public ComplianceOfficer getAssignedTo() { return assignedTo; }
    public void setAssignedTo(ComplianceOfficer assignedTo) { this.assignedTo = assignedTo; }
}