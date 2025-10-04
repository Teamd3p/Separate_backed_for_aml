package com.tss.aml.dto.response;

import java.time.LocalDateTime;

import com.tss.aml.entity.Alert.InvestigationStatus;
import com.tss.aml.entity.enums.AlertStatus;

public class AlertSummaryResponseDto {

    private Long alertId;
    private Long transactionId;
    private String customerName;
    private String ruleTriggered;
    private Integer riskScore;
    private AlertStatus status;
    private InvestigationStatus investigationStatus;
    private LocalDateTime createdAt;

    // Constructors
    public AlertSummaryResponseDto() {}

    public AlertSummaryResponseDto(Long alertId, Long transactionId, String customerName,
                                  String ruleTriggered, Integer riskScore, AlertStatus status,
                                  InvestigationStatus investigationStatus, LocalDateTime createdAt) {
        this.alertId = alertId;
        this.transactionId = transactionId;
        this.customerName = customerName;
        this.ruleTriggered = ruleTriggered;
        this.riskScore = riskScore;
        this.status = status;
        this.investigationStatus = investigationStatus;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getAlertId() { return alertId; }
    public void setAlertId(Long alertId) { this.alertId = alertId; }

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getRuleTriggered() { return ruleTriggered; }
    public void setRuleTriggered(String ruleTriggered) { this.ruleTriggered = ruleTriggered; }

    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }

    public AlertStatus getStatus() { return status; }
    public void setStatus(AlertStatus status) { this.status = status; }

    public InvestigationStatus getInvestigationStatus() { return investigationStatus; }
    public void setInvestigationStatus(InvestigationStatus investigationStatus) { this.investigationStatus = investigationStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
