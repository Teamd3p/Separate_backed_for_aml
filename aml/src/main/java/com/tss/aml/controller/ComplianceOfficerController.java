package com.tss.aml.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tss.aml.dto.InvestigationActionRequest;
import com.tss.aml.dto.SarRequest;
import com.tss.aml.entity.Alert;
import com.tss.aml.entity.Sar;
import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.User;
import com.tss.aml.service.ComplianceOfficerService;

@RestController
@RequestMapping("/api/compliance")
@PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'ADMIN')")
public class ComplianceOfficerController {

    @Autowired
    private ComplianceOfficerService complianceService;

    // === ALERTS ===
    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getAllAlerts() {
        return ResponseEntity.ok(complianceService.getAllAlerts());
    }

    @PostMapping("/alerts/{alertId}/assign")
    public ResponseEntity<Alert> assignAlert(@PathVariable Long alertId, Authentication auth) {
        Long officerId = ((User) auth.getPrincipal()).getUserId();
        return ResponseEntity.ok(complianceService.assignAlertToOfficer(alertId, officerId));
    }

    @GetMapping("/alerts/{alertId}")
    public ResponseEntity<Alert> getAlertDetails(@PathVariable Long alertId) {
        return ResponseEntity.ok(complianceService.getAlertDetails(alertId));
    }

    // === TRANSACTIONS ===
    @GetMapping("/customers/{customerId}/transactions")
    public ResponseEntity<List<Transaction>> getCustomerTransactions(@PathVariable Long customerId) {
        return ResponseEntity.ok(complianceService.getCustomerTransactions(customerId));
    }

    // === INVESTIGATION ===
    @PostMapping("/alerts/{alertId}/action")
    public ResponseEntity<Alert> takeAction(@PathVariable Long alertId, 
                                          @RequestBody InvestigationActionRequest request,
                                          Authentication auth) {
        Long officerId = ((User) auth.getPrincipal()).getUserId();
        return ResponseEntity.ok(complianceService.takeActionOnAlert(alertId, officerId, request));
    }

    // === SAR ===
    @PostMapping("/alerts/{alertId}/sar")
    public ResponseEntity<Sar> generateSar(@PathVariable Long alertId,
                                         @RequestBody SarRequest request,
                                         Authentication auth) {
        Long officerId = ((User) auth.getPrincipal()).getUserId();
        return ResponseEntity.ok(complianceService.generateSar(alertId, officerId, request));
    }

    @PostMapping("/sars/{sarId}/submit")
    public ResponseEntity<Sar> submitSar(@PathVariable Long sarId) {
        return ResponseEntity.ok(complianceService.submitSar(sarId));
    }
}