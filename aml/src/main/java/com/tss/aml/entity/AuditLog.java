package com.tss.aml.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "audit_logs")

public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @NotNull
    private String action; // e.g., "RULE_CREATED", "ALERT_INVESTIGATED", "TRANSACTION_BLOCKED"

    @NotNull
    private String resourceType; // e.g., "Transaction", "Rule", "Alert"

    private Long resourceId; // ID of the affected resource

    private Long userId; // Who performed the action (User ID)

    @Lob
    private String details; // JSON or descriptive text of what changed

    @NotNull
    private String ipAddress;

    private LocalDateTime timestamp = LocalDateTime.now();

    // Constructors
    public AuditLog() {}

    public AuditLog(String action, String resourceType, Long resourceId, Long userId, String details, String ipAddress) {
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.userId = userId;
        this.details = details;
        this.ipAddress = ipAddress;
    }

    // Getters & Setters
    public Long getLogId() { return logId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getTimestamp() { return timestamp; }
}