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
	long countByCustomerUserIdAndTimestampAfter(Long userId, LocalDateTime timestamp);

	@Query("SELECT t.amount FROM Transaction t WHERE t.customer.userId = :customerId AND t.timestamp > :timestamp AND t.amount < :amount")
	List<BigDecimal> findAmountsByCustomerIdAndTimestampAfterAndAmountLessThan(@Param("customerId") Long customerId,
			@Param("timestamp") LocalDateTime timestamp, @Param("amount") BigDecimal amount);

	@Query("SELECT t FROM Transaction t WHERE t.senderAccount.accountNumber = :senderAccountNumber OR t.receiverAccount.accountNumber = :receiverAccountNumber")
	List<Transaction> findBySenderAccountAccountNumberOrReceiverAccountAccountNumber(
			@Param("senderAccountNumber") String senderAccountNumber,
			@Param("receiverAccountNumber") String receiverAccountNumber);

	List<Transaction> findByCustomerUserId(Long customerId);

	long countByCustomerUserIdAndTimestampAfterAndTransactionTypeAndAmountGreaterThanEqual(Long userId,
			LocalDateTime timestamp, TransactionType type, BigDecimal amount);

	// New methods for customer transaction queries
	List<Transaction> findByCustomerUserIdOrderByCreatedAtDesc(Long customerId);

//	List<Transaction> findByCustomerUserIdAndAccountNumberOrderByCreatedAtDesc(Long customerId, String accountNumber);

	Transaction findByTransactionIdAndCustomerUserId(Long transactionId, Long customerId);

	List<Transaction> findByCustomerUserIdAndStatusInOrderByCreatedAtDesc(Long customerId,
			List<TransactionStatus> statuses);

	Long countByCustomerId(Long customerId);

	Long countByCustomerIdAndStatus(Long customerId, TransactionStatus status);

	@Query("SELECT t FROM Transaction t WHERE t.customer.userId = :customerId AND (t.senderAccount.accountNumber = :accountNumber OR t.receiverAccount.accountNumber = :accountNumber) ORDER BY t.createdAt DESC")
	List<Transaction> findByCustomerUserIdAndAccountNumberOrderByCreatedAtDesc(@Param("customerId") Long customerId,
			@Param("accountNumber") String accountNumber);
}