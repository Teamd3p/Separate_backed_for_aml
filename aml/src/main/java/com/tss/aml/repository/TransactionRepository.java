package com.tss.aml.repository;

import com.tss.aml.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	long countByCustomerUserIdAndTimestampAfter(Long userId, LocalDateTime timestamp);
	
    @Query("SELECT t.amount FROM Transaction t WHERE t.customer.userId = :customerId AND t.timestamp > :timestamp AND t.amount < :amount")
    List<BigDecimal> findAmountsByCustomerIdAndTimestampAfterAndAmountLessThan(
        @Param("customerId") Long customerId,
        @Param("timestamp") LocalDateTime timestamp,
        @Param("amount") BigDecimal amount
    );
    
    @Query("SELECT t FROM Transaction t WHERE t.senderAccount.accountNumber = :senderAccountNumber OR t.receiverAccount.accountNumber = :receiverAccountNumber")
    List<Transaction> findBySenderAccountAccountNumberOrReceiverAccountAccountNumber(
        @Param("senderAccountNumber") String senderAccountNumber,
        @Param("receiverAccountNumber") String receiverAccountNumber
    );
}