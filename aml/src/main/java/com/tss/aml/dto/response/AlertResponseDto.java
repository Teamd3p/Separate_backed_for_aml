package com.tss.aml.dto.response;

import java.time.LocalDateTime;

import com.tss.aml.entity.Alert.InvestigationStatus;
import com.tss.aml.entity.enums.AlertStatus;

public class AlertResponseDto {

    private Long alertId;
    private Long transactionId;
    private Long customerId;
    private String customerName;
    private String ruleTriggered;
    private Integer riskScore;
    private AlertStatus status;
    private InvestigationStatus investigationStatus;
    private String assignedToOfficer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public AlertResponseDto() {}

    public AlertResponseDto(Long alertId, Long transactionId, Long customerId, String customerName,
                           String ruleTriggered, Integer riskScore, AlertStatus status,
                           InvestigationStatus investigationStatus, String assignedToOfficer,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.alertId = alertId;
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.ruleTriggered = ruleTriggered;
        this.riskScore = riskScore;
        this.status = status;
        this.investigationStatus = investigationStatus;
        this.assignedToOfficer = assignedToOfficer;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getAlertId() { return alertId; }
    public void setAlertId(Long alertId) { this.alertId = alertId; }

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

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

    public String getAssignedToOfficer() { return assignedToOfficer; }
    public void setAssignedToOfficer(String assignedToOfficer) { this.assignedToOfficer = assignedToOfficer; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
