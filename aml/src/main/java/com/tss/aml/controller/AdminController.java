package com.tss.aml.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tss.aml.dto.request.AccountStatusUpdateRequest;
import com.tss.aml.dto.request.ComplianceOfficerRequest;
import com.tss.aml.dto.request.KeywordRequest;
import com.tss.aml.dto.request.RiskyCountryRequest;
import com.tss.aml.dto.request.RuleRequest;
import com.tss.aml.dto.response.DashboardStatsDto;
import com.tss.aml.entity.AuditLog;
import com.tss.aml.entity.ComplianceOfficer;
import com.tss.aml.entity.HelpDeskTicket;
import com.tss.aml.entity.RiskyCountry;
import com.tss.aml.entity.Rule;
import com.tss.aml.entity.SuspiciousKeyword;
import com.tss.aml.entity.enums.RuleType;
import com.tss.aml.service.AdminService;
import com.tss.aml.service.HelpDeskService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")

public class AdminController {

    @Autowired
    private AdminService adminService;
    
    @Autowired
    private HelpDeskService helpDeskService;

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
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        return ResponseEntity.ok(adminService.getSystemHealthStatus());
    }

    // === HELPDESK MANAGEMENT ===
    @GetMapping("/helpdesk/tickets")
    public ResponseEntity<List<HelpDeskTicket>> getAllHelpDeskTickets(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(helpDeskService.getAllTickets(status));
    }

    @PostMapping("/helpdesk/tickets/{ticketId}/assign")
    public ResponseEntity<HelpDeskTicket> assignHelpDeskTicket(
            @PathVariable Long ticketId,
            @RequestParam Long adminId) {
        return ResponseEntity.ok(helpDeskService.assignTicket(ticketId, adminId));
    }

    @PostMapping("/helpdesk/tickets/{ticketId}/resolve")
    public ResponseEntity<HelpDeskTicket> resolveHelpDeskTicket(
            @PathVariable Long ticketId,
            @RequestBody String resolution) {
        return ResponseEntity.ok(helpDeskService.resolveTicket(ticketId, resolution));
    }

    @DeleteMapping("/helpdesk/tickets/{ticketId}")
    public ResponseEntity<Void> deleteHelpDeskTicket(@PathVariable Long ticketId) {
        helpDeskService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }

    // === USER MANAGEMENT ===
    @GetMapping("/customers")
    public ResponseEntity<List<com.tss.aml.entity.Customer>> getAllCustomers() {
        return ResponseEntity.ok(adminService.getAllCustomers());
    }

    @PutMapping("/customers/{customerId}/status")
    public ResponseEntity<String> updateCustomerStatus(
            @PathVariable Long customerId,
            @RequestBody Map<String, Object> request) {
        String status = (String) request.get("status");
        String reason = (String) request.get("reason");
        
        // Convert status to UserStatus enum
        com.tss.aml.entity.enums.UserStatus userStatus;
        try {
            userStatus = com.tss.aml.entity.enums.UserStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + status);
        }
        
        adminService.updateCustomerStatus(customerId, userStatus);
        return ResponseEntity.ok("Customer status updated successfully");
    }

    // === OFFICER STATUS MANAGEMENT ===
    @PutMapping("/officers/{officerId}/status")
    public ResponseEntity<String> updateOfficerStatus(
            @PathVariable Long officerId,
            @RequestBody Map<String, Object> statusRequest) {
        
        com.tss.aml.entity.enums.UserStatus userStatus;
        
        // Handle different request formats for officer status updates
        if (statusRequest.containsKey("status")) {
            String status = (String) statusRequest.get("status");
            try {
                userStatus = com.tss.aml.entity.enums.UserStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid status: " + status);
            }
        } else if (statusRequest.containsKey("isActive")) {
            Boolean isActive = (Boolean) statusRequest.get("isActive");
            userStatus = isActive ? com.tss.aml.entity.enums.UserStatus.ACTIVE 
                                 : com.tss.aml.entity.enums.UserStatus.INACTIVE;
        } else if (statusRequest.containsKey("active")) {
            Boolean active = (Boolean) statusRequest.get("active");
            userStatus = active ? com.tss.aml.entity.enums.UserStatus.ACTIVE 
                               : com.tss.aml.entity.enums.UserStatus.INACTIVE;
        } else {
            return ResponseEntity.badRequest().body("Missing status field in request");
        }
        
        adminService.updateOfficerStatus(officerId, userStatus);
        return ResponseEntity.ok("Officer status updated successfully");
    }
    
 // PATCH endpoint for officer status
    @PatchMapping("/officers/{officerId}/status")
    public ResponseEntity<String> patchOfficerStatus(
            @PathVariable Long officerId,
            @RequestBody Map<String, Object> statusRequest) {
        return updateOfficerStatus(officerId, statusRequest);
    }

    // PATCH endpoint for customer status  
    @PatchMapping("/customers/{customerId}/status")
    public ResponseEntity<String> patchCustomerStatus(
            @PathVariable Long customerId,
            @RequestBody Map<String, Object> request) {
        return updateCustomerStatus(customerId, request);
    }
}