package com.tss.aml.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tss.aml.dto.AlertDTO;
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
    public ResponseEntity<List<AlertDTO>> getAllAlerts() {
        List<AlertDTO> alerts = complianceService.getAllAlerts()
            .stream()
            .map(AlertDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/alerts/{alertId}/assign")
    public ResponseEntity<AlertDTO> assignAlert(@PathVariable Long alertId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        Long officerId = ((User) auth.getPrincipal()).getUserId();
        Alert alert = complianceService.assignAlertToOfficer(alertId, officerId);
        return ResponseEntity.ok(new AlertDTO(alert));
    }

    @GetMapping("/alerts/{alertId}")
    public ResponseEntity<AlertDTO> getAlertDetails(@PathVariable Long alertId) {
        Alert alert = complianceService.getAlertDetails(alertId);
        return ResponseEntity.ok(new AlertDTO(alert));
    }

    // === TRANSACTIONS ===
    @GetMapping("/customers/{customerId}/transactions")
    public ResponseEntity<List<Transaction>> getCustomerTransactions(@PathVariable Long customerId) {
        return ResponseEntity.ok(complianceService.getCustomerTransactions(customerId));
    }

    // === INVESTIGATION ===
    @PostMapping("/alerts/{alertId}/action")
    public ResponseEntity<AlertDTO> takeAction(@PathVariable Long alertId, 
                                          @RequestBody InvestigationActionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        Long officerId = ((User) auth.getPrincipal()).getUserId();
        Alert alert = complianceService.takeActionOnAlert(alertId, officerId, request);
        return ResponseEntity.ok(new AlertDTO(alert));
    }

    // === SAR ===
    @PostMapping("/alerts/{alertId}/sar")
    public ResponseEntity<Sar> generateSar(@PathVariable Long alertId,
                                         @RequestBody SarRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        Long officerId = ((User) auth.getPrincipal()).getUserId();
        return ResponseEntity.ok(complianceService.generateSar(alertId, officerId, request));
    }

    @PostMapping("/sars/{sarId}/submit")
    public ResponseEntity<Sar> submitSar(@PathVariable Long sarId) {
        return ResponseEntity.ok(complianceService.submitSar(sarId));
    }
}