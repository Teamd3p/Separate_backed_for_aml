package com.tss.aml.service;

import java.util.List;

import com.tss.aml.dto.InvestigationActionRequest;
import com.tss.aml.dto.SarRequest;
import com.tss.aml.entity.Alert;
import com.tss.aml.entity.Sar;
import com.tss.aml.entity.Transaction;

public interface ComplianceOfficerService {
    // Alert Management
    List<Alert> getAllAlerts();
    Alert assignAlertToOfficer(Long alertId, Long officerId);
    Alert getAlertDetails(Long alertId);
    List<Transaction> getCustomerTransactions(Long customerId);

    // Investigation
    Alert takeActionOnAlert(Long alertId, Long officerId, InvestigationActionRequest request);

    // SAR
    Sar generateSar(Long alertId, Long officerId, SarRequest request);
    Sar submitSar(Long sarId);
}