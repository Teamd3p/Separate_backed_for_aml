package com.tss.aml.service;

import com.tss.aml.entity.AuditAction;
import com.tss.aml.entity.AuditResourceType;
import com.tss.aml.entity.AuditStatus;

public interface AuditService {
    void logAction(AuditAction action, AuditResourceType resourceType, Long resourceId, 
                   Long userId, String username, String details, String ipAddress, String userAgent);
    
    void logAction(AuditAction action, AuditResourceType resourceType, Long resourceId, 
                   Long userId, String username, String details, String ipAddress, String userAgent, AuditStatus status);
    
    void logSuccess(AuditAction action, AuditResourceType resourceType, Long resourceId, 
                    Long userId, String username, String details, String ipAddress);
    
    void logFailure(AuditAction action, AuditResourceType resourceType, Long resourceId, 
                    Long userId, String username, String details, String ipAddress);
}
