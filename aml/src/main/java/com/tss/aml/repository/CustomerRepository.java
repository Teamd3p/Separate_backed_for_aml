package com.tss.aml.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tss.aml.entity.Customer;
import com.tss.aml.entity.enums.KycStatus;
import com.tss.aml.entity.enums.UserStatus;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Add custom queries later if needed, e.g.:
    // Customer findByEmail(String email);
    
    List<Customer> findByKycStatus(KycStatus kycStatus);
    
    long countByKycStatus(KycStatus kycStatus);
    
    // New methods for admin dashboard
    long countByStatus(UserStatus status);
    
    Optional<Customer> findByEmail(String email); // 

}