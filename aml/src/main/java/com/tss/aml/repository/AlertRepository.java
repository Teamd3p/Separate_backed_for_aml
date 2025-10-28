package com.tss.aml.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tss.aml.entity.Alert;
import com.tss.aml.entity.enums.AlertStatus;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByCustomerUserIdOrderByCreatedAtDesc(Long customerId);
    
    List<Alert> findByAssignedToUserIdOrderByCreatedAtDesc(Long officerId);
    
    // New methods for enhanced alert queries
    List<Alert> findByStatusOrderByCreatedAtDesc(AlertStatus status);
    List<Alert> findByRiskScoreBetweenOrderByRiskScoreDesc(Integer minRiskScore, Integer maxRiskScore);
    
    // Count methods for admin dashboard
    long countByStatus(AlertStatus status);
    long countByRiskScoreGreaterThanEqual(Integer riskScore);
    long countByCustomerUserId(Long customerId);
}