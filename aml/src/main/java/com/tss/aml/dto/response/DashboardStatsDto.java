package com.tss.aml.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DashboardStatsDto {
    private Long totalCustomers;
    private Long activeCustomers;
    private Long totalTransactions;
    private Long pendingAlerts;
    private Long highRiskAlerts;
    private Long totalComplianceOfficers;
    private Long pendingKycDocuments;
    private Long activeRules;

    // Constructors
    public DashboardStatsDto() {}

    // Getters and Setters
    public Long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(Long totalCustomers) { this.totalCustomers = totalCustomers; }

    public Long getActiveCustomers() { return activeCustomers; }
    public void setActiveCustomers(Long activeCustomers) { this.activeCustomers = activeCustomers; }

    public Long getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Long totalTransactions) { this.totalTransactions = totalTransactions; }

    public Long getPendingAlerts() { return pendingAlerts; }
    public void setPendingAlerts(Long pendingAlerts) { this.pendingAlerts = pendingAlerts; }

    public Long getHighRiskAlerts() { return highRiskAlerts; }
    public void setHighRiskAlerts(Long highRiskAlerts) { this.highRiskAlerts = highRiskAlerts; }

    public Long getTotalComplianceOfficers() { return totalComplianceOfficers; }
    public void setTotalComplianceOfficers(Long totalComplianceOfficers) { this.totalComplianceOfficers = totalComplianceOfficers; }

    public Long getPendingKycDocuments() { return pendingKycDocuments; }
    public void setPendingKycDocuments(Long pendingKycDocuments) { this.pendingKycDocuments = pendingKycDocuments; }

    public Long getActiveRules() { return activeRules; }
    public void setActiveRules(Long activeRules) { this.activeRules = activeRules; }
}
