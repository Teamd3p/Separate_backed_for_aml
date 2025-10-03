package com.tss.aml.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.aml.dto.InvestigationActionRequest;
import com.tss.aml.dto.SarRequest;
import com.tss.aml.entity.Alert;
import com.tss.aml.entity.ComplianceOfficer;
import com.tss.aml.entity.Role;
import com.tss.aml.entity.Sar;
import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.TransactionStatus;
import com.tss.aml.repository.AlertRepository;
import com.tss.aml.repository.ComplianceOfficerRepository;
import com.tss.aml.repository.SarRepository;
import com.tss.aml.repository.TransactionRepository;
import com.tss.aml.service.ComplianceOfficerService;

@Service
@Transactional
public class ComplianceOfficerServiceImpl implements ComplianceOfficerService {

    @Autowired
    private AlertRepository alertRepo;

    @Autowired
    private ComplianceOfficerRepository officerRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private SarRepository sarRepo;

    // === ALERTS ===
    @Override
    public List<Alert> getAllAlerts() {
        return alertRepo.findAll();
    }

    @Override
    public Alert assignAlertToOfficer(Long alertId, Long officerId) {
        Alert alert = alertRepo.findById(alertId).orElseThrow(() -> new RuntimeException("Alert not found"));
        ComplianceOfficer officer = officerRepo.findById(officerId).orElseThrow(() -> new RuntimeException("Officer not found"));
        alert.setAssignedTo(officer);
        alert.setInvestigationStatus(Alert.InvestigationStatus.INVESTIGATING);
        return alertRepo.save(alert);
    }

    @Override
    public Alert getAlertDetails(Long alertId) {
        return alertRepo.findById(alertId).orElseThrow(() -> new RuntimeException("Alert not found"));
    }

    @Override
    public List<Transaction> getCustomerTransactions(Long customerId) {
        return transactionRepo.findByCustomerUserId(customerId);
    }

    // === INVESTIGATION ===
    @Override
    public Alert takeActionOnAlert(Long alertId, Long officerId, InvestigationActionRequest request) {
        Alert alert = alertRepo.findById(alertId).orElseThrow(() -> new RuntimeException("Alert not found"));
        ComplianceOfficer officer = officerRepo.findById(officerId).orElseThrow(() -> new RuntimeException("Officer not found"));

        ComplianceOfficer assignedOfficer = alert.getAssignedTo();
        if (assignedOfficer != null && 
            !officer.getUserId().equals(assignedOfficer.getUserId()) && 
            !officer.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Not authorized...");
        }

        Alert.InvestigationStatus status = Alert.InvestigationStatus.valueOf(request.getDecision());
        alert.setInvestigationStatus(status);
        // Update transaction status based on decision
        if (status == Alert.InvestigationStatus.TRUE_POSITIVE) {
            alert.getTransaction().setStatus(TransactionStatus.BLOCKED);
        } else if (status == Alert.InvestigationStatus.FALSE_POSITIVE) {
            alert.getTransaction().setStatus(TransactionStatus.COMPLETED);
        }

        // Auto-generate SAR if true positive
        if (status == Alert.InvestigationStatus.TRUE_POSITIVE && request.getSarSummary() != null) {
            Sar sar = new Sar(alert, officer, request.getSarSummary());
            sarRepo.save(sar);
        }

        return alertRepo.save(alert);
    }

    // === SAR ===
    @Override
    public Sar generateSar(Long alertId, Long officerId, SarRequest request) {
        Alert alert = alertRepo.findById(alertId).orElseThrow(() -> new RuntimeException("Alert not found"));
        ComplianceOfficer officer = officerRepo.findById(officerId).orElseThrow(() -> new RuntimeException("Officer not found"));
        Sar sar = new Sar(alert, officer, request.getSummary());
        return sarRepo.save(sar);
    }

    @Override
    public Sar submitSar(Long sarId) {
        Sar sar = sarRepo.findById(sarId).orElseThrow(() -> new RuntimeException("SAR not found"));
        sar.setStatus(Sar.SarStatus.SUBMITTED);
        sar.setSubmittedAt(LocalDateTime.now());
        return sarRepo.save(sar);
    }
}