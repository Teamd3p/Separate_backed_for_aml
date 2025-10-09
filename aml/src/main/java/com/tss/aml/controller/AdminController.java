package com.tss.aml.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tss.aml.dto.request.AccountStatusUpdateRequest;
import com.tss.aml.dto.request.ComplianceOfficerRequest;
import com.tss.aml.dto.request.KeywordRequest;
import com.tss.aml.dto.request.RiskyCountryRequest;
import com.tss.aml.dto.request.RuleRequest;
import com.tss.aml.dto.response.DashboardStatsDto;
import com.tss.aml.entity.AuditLog;
import com.tss.aml.entity.ComplianceOfficer;
import com.tss.aml.entity.RiskyCountry;
import com.tss.aml.entity.Rule;
import com.tss.aml.entity.SuspiciousKeyword;
import com.tss.aml.entity.enums.RuleType;
import com.tss.aml.service.AdminService;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // === COMPLIANCE OFFICERS ===
    @PostMapping("/officers")
    public ResponseEntity<ComplianceOfficer> createOfficer(@RequestBody ComplianceOfficerRequest request) {
        return ResponseEntity.ok(adminService.createComplianceOfficer(request));
    }

    @GetMapping("/officers")
    public ResponseEntity<List<ComplianceOfficer>> getAllOfficers() {
        return ResponseEntity.ok(adminService.getAllComplianceOfficers());
    }

    @DeleteMapping("/officers/{id}")
    public ResponseEntity<Void> deleteOfficer(@PathVariable Long id) {
        adminService.deleteComplianceOfficer(id);
        return ResponseEntity.noContent().build();
    }

    // === RULES ===
    @PostMapping("/rules")
    public ResponseEntity<Rule> createRule(@RequestBody RuleRequest request) {
        return ResponseEntity.ok(adminService.createRule(request));
    }

    @PutMapping("/rules/{id}")
    public ResponseEntity<Rule> updateRule(@PathVariable Long id, @RequestBody RuleRequest request) {
        return ResponseEntity.ok(adminService.updateRule(id, request));
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        adminService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rules")
    public ResponseEntity<List<Rule>> getAllRules() {
        return ResponseEntity.ok(adminService.getAllRules());
    }

    // === KEYWORDS ===
    @PostMapping("/keywords")
    public ResponseEntity<SuspiciousKeyword> createKeyword(@RequestBody KeywordRequest request) {
        return ResponseEntity.ok(adminService.createKeyword(request));
    }

    @PutMapping("/keywords/{id}")
    public ResponseEntity<SuspiciousKeyword> updateKeyword(@PathVariable Long id, @RequestBody KeywordRequest request) {
        return ResponseEntity.ok(adminService.updateKeyword(id, request));
    }

    @DeleteMapping("/keywords/{id}")
    public ResponseEntity<Void> deleteKeyword(@PathVariable Long id) {
        adminService.deleteKeyword(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/keywords")
    public ResponseEntity<List<SuspiciousKeyword>> getAllKeywords() {
        return ResponseEntity.ok(adminService.getAllKeywords());
    }

    // === RISKY COUNTRIES ===
    @PostMapping("/countries")
    public ResponseEntity<RiskyCountry> createCountry(@RequestBody RiskyCountryRequest request) {
        return ResponseEntity.ok(adminService.createRiskyCountry(request));
    }

    @PutMapping("/countries/{code}")
    public ResponseEntity<RiskyCountry> updateCountry(@PathVariable String code, @RequestBody RiskyCountryRequest request) {
        return ResponseEntity.ok(adminService.updateRiskyCountry(code, request));
    }

    @DeleteMapping("/countries/{code}")
    public ResponseEntity<Void> deleteCountry(@PathVariable String code) {
        adminService.deleteRiskyCountry(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/countries")
    public ResponseEntity<List<RiskyCountry>> getAllCountries() {
        return ResponseEntity.ok(adminService.getAllRiskyCountries());
    }

    // === DASHBOARD & STATISTICS ===
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/alerts/count/customer/{customerId}")
    public ResponseEntity<Long> getAlertCountByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(adminService.getAlertCountByCustomerId(customerId));
    }

    // === AUDIT LOGS ===
    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(adminService.getAllAuditLogs(page, size));
    }

    // === CUSTOMER MANAGEMENT ===
    @PutMapping("/customers/{customerId}/account-status")
    public ResponseEntity<String> updateCustomerAccountStatus(
            @PathVariable Long customerId,
            @RequestBody AccountStatusUpdateRequest request) {
        adminService.updateCustomerAccountStatus(customerId, request.getStatus(), request.getReason());
        return ResponseEntity.ok("Account status updated successfully");
    }

    // === RULES BY TYPE ===
    @GetMapping("/rules/type/{ruleType}")
    public ResponseEntity<List<Rule>> getRulesByType(
            @PathVariable RuleType ruleType) {
        return ResponseEntity.ok(adminService.getRulesByType(ruleType));
    }

    // === SYSTEM HEALTH ===
    @GetMapping("/system/health")
    public ResponseEntity<String> getSystemHealth() {
        return ResponseEntity.ok(adminService.getSystemHealthStatus());
    }
}