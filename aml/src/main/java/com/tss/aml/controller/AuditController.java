package com.tss.aml.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// // import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tss.aml.entity.AuditLog;
import com.tss.aml.entity.enums.AuditAction;
import com.tss.aml.entity.enums.AuditResourceType;
import com.tss.aml.repository.AuditLogRepository;
import com.tss.aml.service.AuditService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/admin/audit")
public class AuditController {

    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private AuditService auditService;

    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            HttpServletRequest request) {
        
        String ipAddress = getClientIpAddress(request);
        
        // For simplicity, returning all logs. In production, implement pagination
        List<AuditLog> logs = auditLogRepository.findAll();
        
        auditService.logSuccess(AuditAction.DATA_VIEWED, AuditResourceType.AUDIT_LOG, null, 
            null, null, "Audit logs viewed by admin", ipAddress);
        
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByUser(
            @PathVariable Long userId,
            HttpServletRequest request) {
        
        String ipAddress = getClientIpAddress(request);
        
        List<AuditLog> logs = auditLogRepository.findByUserId(userId);
        
        auditService.logSuccess(AuditAction.DATA_VIEWED, AuditResourceType.AUDIT_LOG, null, 
            null, null, "Audit logs viewed for user: " + userId, ipAddress);
        
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/action/{action}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByAction(
            @PathVariable AuditAction action,
            HttpServletRequest request) {
        
        String ipAddress = getClientIpAddress(request);
        
        List<AuditLog> logs = auditLogRepository.findByAction(action);
        
        auditService.logSuccess(AuditAction.DATA_VIEWED, AuditResourceType.AUDIT_LOG, null, 
            null, null, "Audit logs viewed for action: " + action, ipAddress);
        
        return ResponseEntity.ok(logs);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
<<<<<<< HEAD

//    @GetMapping("/logs/resource/{resourceType}")
//    public ResponseEntity<List<AuditLog>> getAuditLogsByResourceType(
//            @PathVariable AuditResourceType resourceType,
//            HttpServletRequest request) {
//        
//        String ipAddress = getClientIpAddress(request);
//        
//        List<AuditLog> logs = auditLogRepository.findByResourceType(resourceType);
//        
//        auditService.logSuccess(AuditAction.DATA_VIEWED, AuditResourceType.AUDIT_LOG, null, 
//            null, null, "Audit logs viewed for resource type: " + resourceType, ipAddress);
//        
//        return ResponseEntity.ok(logs);
//    }
//
//    @GetMapping("/logs/date-range")
//    public ResponseEntity<List<AuditLog>> getAuditLogsByDateRange(
//            @RequestParam String startDate,
//            @RequestParam String endDate,
//            HttpServletRequest request) {
//        
//        String ipAddress = getClientIpAddress(request);
//        
//        try {
//            java.time.LocalDateTime start = java.time.LocalDateTime.parse(startDate);
//            java.time.LocalDateTime end = java.time.LocalDateTime.parse(endDate);
//            
//            List<AuditLog> logs = auditLogRepository.findByTimestampBetween(start, end);
//            
//            auditService.logSuccess(AuditAction.DATA_VIEWED, AuditResourceType.AUDIT_LOG, null, 
//                null, null, "Audit logs viewed for date range: " + startDate + " to " + endDate, ipAddress);
//            
//            return ResponseEntity.ok(logs);
//        } catch (Exception e) {
//            auditService.logFailure(AuditAction.DATA_VIEWED, AuditResourceType.AUDIT_LOG, null, 
//                null, null, "Failed to parse date range: " + e.getMessage(), ipAddress);
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @GetMapping("/logs/status/{status}")
//    public ResponseEntity<List<AuditLog>> getAuditLogsByStatus(
//            @PathVariable com.tss.aml.entity.enums.AuditStatus status,
//            HttpServletRequest request) {
//        
//        String ipAddress = getClientIpAddress(request);
//        
//        List<AuditLog> logs = auditLogRepository.findByStatus(status);
//        
//        auditService.logSuccess(AuditAction.DATA_VIEWED, AuditResourceType.AUDIT_LOG, null, 
//            null, null, "Audit logs viewed for status: " + status, ipAddress);
//        
//        return ResponseEntity.ok(logs);
//    }
=======
>>>>>>> 3d7d8a1cd41cfa13dbead653ab5dda9af9c601af
}
