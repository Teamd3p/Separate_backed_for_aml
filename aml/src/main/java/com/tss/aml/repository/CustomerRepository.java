package com.tss.aml.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tss.aml.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Add custom queries later if needed, e.g.:
    // Customer findByEmail(String email);
	
    Optional<Customer> findByEmail(String email); // 👈 ADD THIS

}