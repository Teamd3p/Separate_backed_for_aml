package com.tss.aml.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
=======
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
>>>>>>> 3d7d8a1cd41cfa13dbead653ab5dda9af9c601af

import com.tss.aml.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
<<<<<<< HEAD
	List<Account> findByCustomerUserId(Long customerId);

=======
    
    @Query(value = "SELECT * FROM accounts WHERE customer_id = :customerId", nativeQuery = true)
    List<Account> findByCustomerUserId(@Param("customerId") Long customerId);
>>>>>>> 3d7d8a1cd41cfa13dbead653ab5dda9af9c601af
}