package com.tss.aml.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TransactionCountDto {
    private Long totalTransactions;
    private Long flaggedTransactions;
    private Long blockedTransactions;
    private Long completedTransactions;
    private Long pendingTransactions;

    // Constructors
    public TransactionCountDto() {}

    // Getters and Setters
    public Long getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Long totalTransactions) { this.totalTransactions = totalTransactions; }

    public Long getFlaggedTransactions() { return flaggedTransactions; }
    public void setFlaggedTransactions(Long flaggedTransactions) { this.flaggedTransactions = flaggedTransactions; }

    public Long getBlockedTransactions() { return blockedTransactions; }
    public void setBlockedTransactions(Long blockedTransactions) { this.blockedTransactions = blockedTransactions; }

    public Long getCompletedTransactions() { return completedTransactions; }
    public void setCompletedTransactions(Long completedTransactions) { this.completedTransactions = completedTransactions; }

    public Long getPendingTransactions() { return pendingTransactions; }
    public void setPendingTransactions(Long pendingTransactions) { this.pendingTransactions = pendingTransactions; }
}
