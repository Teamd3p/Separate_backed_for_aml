package com.tss.aml.dto;

import java.time.LocalDateTime;

import com.tss.aml.entity.Alert;
import com.tss.aml.entity.enums.AlertStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {
    private Long alertId;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Long transactionId;
    private String ruleTriggered;
    private Integer riskScore;
    private AlertStatus status;
    private LocalDateTime createdAt;
    private Long assignedToId;
    private String assignedToName;
    private Alert.InvestigationStatus investigationStatus;

    // Constructor from Alert entity
    public AlertDTO(Alert alert) {
        this.alertId = alert.getAlertId();
        this.customerId = alert.getCustomer() != null ? alert.getCustomer().getUserId() : null;
        this.customerName = alert.getCustomer() != null ? 
            alert.getCustomer().getFirstName() + " " + alert.getCustomer().getLastName() : null;
        this.customerEmail = alert.getCustomer() != null ? alert.getCustomer().getEmail() : null;
        this.transactionId = alert.getTransaction() != null ? alert.getTransaction().getTransactionId() : null;
        this.ruleTriggered = alert.getRuleTriggered();
        this.riskScore = alert.getRiskScore();
        this.status = alert.getStatus();
        this.createdAt = alert.getCreatedAt();
        this.assignedToId = alert.getAssignedTo() != null ? alert.getAssignedTo().getUserId() : null;
        this.assignedToName = alert.getAssignedTo() != null ? 
            alert.getAssignedTo().getFirstName() + " " + alert.getAssignedTo().getLastName() : null;
        this.investigationStatus = alert.getInvestigationStatus();
    }
}
