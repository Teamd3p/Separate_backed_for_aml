package com.tss.aml.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.enums.TransactionStatus;
import com.tss.aml.entity.enums.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Count transactions for a customer after a given timestamp
    long countByCustomerUserIdAndTimestampAfter(Long userId, LocalDateTime timestamp);

    // Find amounts for a customer after timestamp and below a certain amount
    @Query("SELECT t.amount FROM Transaction t WHERE t.customer.userId = :customerId AND t.timestamp > :timestamp AND t.amount < :amount")
    List<BigDecimal> findAmountsByCustomerIdAndTimestampAfterAndAmountLessThan(
            @Param("customerId") Long customerId,
            @Param("timestamp") LocalDateTime timestamp,
            @Param("amount") BigDecimal amount);

    // Find transactions by sender or receiver account number
    @Query("SELECT t FROM Transaction t WHERE t.senderAccount.accountNumber = :senderAccountNumber OR t.receiverAccount.accountNumber = :receiverAccountNumber")
    List<Transaction> findBySenderAccountAccountNumberOrReceiverAccountAccountNumber(
            @Param("senderAccountNumber") String senderAccountNumber,
            @Param("receiverAccountNumber") String receiverAccountNumber);

    // Find transactions for a customer
    List<Transaction> findByCustomerUserId(Long customerId);

    // Count transactions with type and minimum amount
    long countByCustomerUserIdAndTimestampAfterAndTransactionTypeAndAmountGreaterThanEqual(
            Long userId, LocalDateTime timestamp, TransactionType type, BigDecimal amount);

    // List transactions for a customer ordered by timestamp
    List<Transaction> findByCustomerUserIdOrderByTimestampDesc(Long customerId);

    // Find transaction by transactionId and customer
    Transaction findByTransactionIdAndCustomerUserId(Long transactionId, Long customerId);

    // Find transactions for a customer filtered by status
    List<Transaction> findByCustomerUserIdAndStatusInOrderByTimestampDesc(Long customerId, List<TransactionStatus> statuses);

    // Count transactions for a customer
    long countByCustomerUserId(Long customerId);

    // Count transactions for a customer filtered by status
    long countByCustomerUserIdAndStatus(Long customerId, TransactionStatus status);

}
