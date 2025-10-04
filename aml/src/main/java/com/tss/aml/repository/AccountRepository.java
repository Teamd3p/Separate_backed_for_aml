package com.tss.aml.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tss.aml.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNumber(String accountNumber);
}