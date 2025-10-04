package com.tss.aml.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tss.aml.entity.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    // Add custom queries later if needed (e.g., findByStatus)
}