package com.tss.aml.repository;

import com.tss.aml.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    // Add custom queries later if needed (e.g., findByStatus)
}