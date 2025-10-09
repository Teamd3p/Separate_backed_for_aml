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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tss.aml.dto.AlertDTO;
import com.tss.aml.dto.SarDTO;
import com.tss.aml.dto.TransactionDTO;
import com.tss.aml.dto.request.InvestigationActionRequest;
import com.tss.aml.dto.request.SarRequest;
import com.tss.aml.entity.Alert;
import com.tss.aml.entity.Sar;
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
    public ResponseEntity<List<TransactionDTO>> getCustomerTransactions(@PathVariable Long customerId) {
        List<TransactionDTO> transactions = complianceService.getCustomerTransactions(customerId)
            .stream()
            .map(TransactionDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(transactions);
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
    public ResponseEntity<SarDTO> generateSar(@PathVariable Long alertId,
                                         @RequestBody SarRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        Long officerId = ((User) auth.getPrincipal()).getUserId();
        Sar sar = complianceService.generateSar(alertId, officerId, request);
        return ResponseEntity.ok(new SarDTO(sar));
    }

    @PostMapping("/sars/{sarId}/submit")
    public ResponseEntity<SarDTO> submitSar(@PathVariable Long sarId) {
        Sar sar = complianceService.submitSar(sarId);
        return ResponseEntity.ok(new SarDTO(sar));
    }

    // === ENHANCED COMPLIANCE OFFICER ENDPOINTS ===
    @GetMapping("/alerts/status/{status}")
    public ResponseEntity<List<com.tss.aml.dto.response.AlertResponseDto>> getAlertsByStatus(
            @PathVariable com.tss.aml.entity.enums.AlertStatus status) {
        
        List<com.tss.aml.entity.Alert> alerts = complianceService.getAlertsByStatus(status);
        List<com.tss.aml.dto.response.AlertResponseDto> responses = alerts.stream()
            .map(this::convertToAlertResponse)
            .collect(java.util.stream.Collectors.toList());
            
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/alerts/risk-score")
    public ResponseEntity<List<com.tss.aml.dto.response.AlertResponseDto>> getAlertsByRiskScore(
            @RequestParam(defaultValue = "50") Integer minRiskScore,
            @RequestParam(defaultValue = "100") Integer maxRiskScore) {
        
        List<com.tss.aml.entity.Alert> alerts = complianceService.getAlertsByRiskScoreRange(minRiskScore, maxRiskScore);
        List<com.tss.aml.dto.response.AlertResponseDto> responses = alerts.stream()
            .map(this::convertToAlertResponse)
            .collect(java.util.stream.Collectors.toList());
            
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/sars")
    public ResponseEntity<List<SarDTO>> getAllSars() {
        List<com.tss.aml.entity.Sar> sars = complianceService.getAllSars();
        List<SarDTO> responses = sars.stream()
            .map(SarDTO::new)
            .collect(java.util.stream.Collectors.toList());
            
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/alerts/{alertId}/rules")
    public ResponseEntity<List<String>> getTriggeredRulesForAlert(@PathVariable Long alertId) {
        List<String> triggeredRules = complianceService.getTriggeredRulesForAlert(alertId);
        return ResponseEntity.ok(triggeredRules);
    }

    @GetMapping("/alerts/history/customer/{customerId}")
    public ResponseEntity<List<com.tss.aml.dto.response.AlertResponseDto>> getAlertHistoryByCustomer(
            @PathVariable Long customerId) {
        
        List<com.tss.aml.entity.Alert> alerts = complianceService.getAlertHistoryByCustomerId(customerId);
        List<com.tss.aml.dto.response.AlertResponseDto> responses = alerts.stream()
            .map(this::convertToAlertResponse)
            .collect(java.util.stream.Collectors.toList());
            
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/alerts/history/officer")
    public ResponseEntity<List<com.tss.aml.dto.response.AlertResponseDto>> getAlertHistoryByOfficer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long officerId = ((User) auth.getPrincipal()).getUserId();
        
        List<com.tss.aml.entity.Alert> alerts = complianceService.getAlertHistoryByOfficerId(officerId);
        List<com.tss.aml.dto.response.AlertResponseDto> responses = alerts.stream()
            .map(this::convertToAlertResponse)
            .collect(java.util.stream.Collectors.toList());
            
        return ResponseEntity.ok(responses);
    }

    // Helper method
    private com.tss.aml.dto.response.AlertResponseDto convertToAlertResponse(com.tss.aml.entity.Alert alert) {
        com.tss.aml.dto.response.AlertResponseDto response = new com.tss.aml.dto.response.AlertResponseDto();
        response.setAlertId(alert.getAlertId());
        response.setCustomerId(alert.getCustomer().getUserId());
        response.setCustomerName(alert.getCustomer().getFirstName() + " " + alert.getCustomer().getLastName());
        response.setTransactionId(alert.getTransaction() != null ? alert.getTransaction().getTransactionId() : null);
        response.setRuleTriggered(alert.getRuleTriggered());
        response.setRiskScore(alert.getRiskScore());
        response.setStatus(alert.getStatus());
        response.setCreatedAt(alert.getCreatedAt());
        response.setAssignedOfficerName(alert.getAssignedTo() != null ? 
            alert.getAssignedTo().getFirstName() + " " + alert.getAssignedTo().getLastName() : null);
        return response;
    }
}