package com.tss.aml.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tss.aml.dto.request.DepositRequest;
import com.tss.aml.dto.request.TransferRequest;
import com.tss.aml.dto.request.WithdrawalRequest;
import com.tss.aml.dto.response.CurrencyConversionResult;
import com.tss.aml.dto.response.TransactionCountDto;
import com.tss.aml.entity.Account;
import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.enums.AuditAction;
import com.tss.aml.entity.enums.AuditResourceType;
import com.tss.aml.entity.enums.AuditStatus;
import com.tss.aml.entity.enums.TransactionStatus;
import com.tss.aml.entity.enums.TransactionType;
import com.tss.aml.exception.UserApiException;
import com.tss.aml.repository.AccountRepository;
import com.tss.aml.repository.TransactionRepository;
import com.tss.aml.rule.RuleEngineResult;
import com.tss.aml.service.AlertService;
import com.tss.aml.service.AuditService;
import com.tss.aml.service.CurrencyService;
import com.tss.aml.service.RuleEngineService;
import com.tss.aml.service.TransactionService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private RuleEngineService ruleEngineService;

	@Autowired
	private AlertService alertService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AuditService auditService;

	@Autowired
	private CurrencyService currencyService;

	@Override
	public Transaction processTransaction(Transaction transaction) {
		logger.info("🔄 Processing transaction: {} | Amount: {} {} | Type: {}", 
			transaction.getTransactionId(), 
			transaction.getAmount(), 
			transaction.getCurrency(),
			transaction.getTransactionType());

		// Save transaction first
		transaction.setStatus(TransactionStatus.PENDING);
		transaction = transactionRepository.save(transaction);
		logger.debug("💾 Transaction saved with ID: {}", transaction.getTransactionId());

		// Evaluate AML rules
		logger.info("🔍 Starting AML rule evaluation for transaction: {}", transaction.getTransactionId());
		RuleEngineResult result = ruleEngineService.evaluate(transaction);

		// Normalize risk score to 0-100 range and set status based on thresholds
		int normalizedRisk = result.getRiskScore(); // Already normalized in RuleEngineService
		
		logger.info("📊 Risk assessment complete - Normalized Risk: {}/100", normalizedRisk);
		
		if (!result.isSuspicious()) {
			transaction.setStatus(TransactionStatus.COMPLETED);
			logger.info("✅ TRANSACTION COMPLETED - Low risk score: {}", normalizedRisk);
		} else if (result.getTotalRiskScore() >= 85) {
			transaction.setStatus(TransactionStatus.BLOCKED);
			logger.warn("🚫 TRANSACTION BLOCKED - High risk score: {}", normalizedRisk);
		} else {
			transaction.setStatus(TransactionStatus.FLAGGED);
			logger.warn("⚠️ TRANSACTION FLAGGED - Medium risk score: {}", normalizedRisk);
		}

		transaction.setRiskScore(normalizedRisk);
		transaction = transactionRepository.save(transaction);

		// Create alert if suspicious
		if (result.isSuspicious()) {
			logger.warn("🚨 Creating alert for suspicious transaction: {} | Triggered rules: {}", 
				transaction.getTransactionId(), result.getTriggeredRules());
			alertService.createAlertForTransaction(transaction, result);
		} else {
			logger.info("✅ No alert needed - transaction is clean");
		}

		logger.info("✅ Transaction processing complete: {} | Final Status: {} | Risk Score: {}", 
			transaction.getTransactionId(), 
			transaction.getStatus(), 
			transaction.getRiskScore());

		return transaction;
	}

	@Override
	public Transaction transferFunds(TransferRequest transferRequest, Long userId, String ipAddress, String userAgent) {
		try {
			// Find sender and receiver accounts
			Account senderAccount = accountRepository.findByAccountNumber(transferRequest.getSenderAccountNumber());
			if (senderAccount == null) {
				auditService.logFailure(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, null, userId, null,
						"Sender account not found: " + transferRequest.getSenderAccountNumber(), ipAddress);
				throw new UserApiException("Sender account not found");
			}

			// SECURITY CHECK: Verify that the logged-in user owns the sender account
			if (!senderAccount.getCustomer().getUserId().equals(userId)) {
				auditService.logFailure(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, null, userId, null,
						"Unauthorized access attempt - user does not own sender account: "
								+ transferRequest.getSenderAccountNumber(),
						ipAddress);
				throw new UserApiException("You are not authorized to transfer from this account");
			}

			Account receiverAccount = accountRepository.findByAccountNumber(transferRequest.getReceiverAccountNumber());
			if (receiverAccount == null) {
				auditService.logFailure(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, null, userId, null,
						"Receiver account not found: " + transferRequest.getReceiverAccountNumber(), ipAddress);
				throw new UserApiException("Receiver account not found");
			}

			// Check if sender has sufficient balance
			if (senderAccount.getBalance().compareTo(transferRequest.getAmount()) < 0) {
				auditService.logFailure(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, null, userId, null,
						"Insufficient balance in sender account", ipAddress);
				throw new UserApiException("Insufficient balance");
			}

			// Validate sender account currency matches request
			if (!senderAccount.getCurrency().equals(transferRequest.getCurrency())) {
				auditService.logFailure(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, null, userId, null,
						"Sender account currency mismatch", ipAddress);
				throw new UserApiException("Transfer currency must match sender account currency");
			}

			// Handle currency conversion if needed
			CurrencyConversionResult conversionResult = null;
			BigDecimal finalAmount = transferRequest.getAmount();
			BigDecimal totalDeductionFromSender = transferRequest.getAmount();
			
			if (!senderAccount.getCurrency().equals(receiverAccount.getCurrency())) {
				logger.info("💱 Cross-currency transfer detected: {} {} → {} {}", 
					transferRequest.getAmount(), senderAccount.getCurrency(), 
					"?", receiverAccount.getCurrency());
				
				// Convert from sender currency to receiver currency
				conversionResult = currencyService.convertCurrency(
					senderAccount.getCurrency(), 
					receiverAccount.getCurrency(), 
					transferRequest.getAmount()
				);
				
				finalAmount = conversionResult.getNetAmount(); // Amount after conversion fee
				totalDeductionFromSender = transferRequest.getAmount().add(conversionResult.getConversionFee());
				
				// Check if sender has sufficient balance including conversion fee
				if (senderAccount.getBalance().compareTo(totalDeductionFromSender) < 0) {
					auditService.logFailure(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, null, userId, null,
							"Insufficient balance for transfer including conversion fee", ipAddress);
					throw new UserApiException("Insufficient balance for transfer including conversion fee of " + 
						conversionResult.getConversionFee() + " " + senderAccount.getCurrency());
				}
				
				logger.info("✅ Currency conversion: {} {} = {} {} (Fee: {} {})", 
					transferRequest.getAmount(), senderAccount.getCurrency(),
					finalAmount, receiverAccount.getCurrency(),
					conversionResult.getConversionFee(), senderAccount.getCurrency());
			}

			// Create transaction
			Transaction transaction = new Transaction();
			transaction.setCustomer(senderAccount.getCustomer());
			transaction.setSenderAccount(senderAccount);
			transaction.setReceiverAccount(receiverAccount);
			transaction.setAmount(finalAmount);
			transaction.setCurrency(receiverAccount.getCurrency());
			transaction.setDescription(transferRequest.getDescription());
			transaction.setTransactionType(TransactionType.TRANSFER);
			transaction.setCountryCode(transferRequest.getCountryCode());
			transaction.setCounterpartyName(
					receiverAccount.getCustomer().getFirstName() + " " + receiverAccount.getCustomer().getLastName());
			transaction.setCounterpartyAccount(receiverAccount.getAccountNumber());
			
			// Set currency conversion details if applicable
			if (conversionResult != null) {
				transaction.setOriginalCurrency(senderAccount.getCurrency());
				transaction.setOriginalAmount(transferRequest.getAmount());
				transaction.setExchangeRate(conversionResult.getExchangeRate());
				transaction.setConversionFee(conversionResult.getConversionFee());
				transaction.setConversionId(conversionResult.getConversionId());
				transaction.setIsCurrencyConverted(true);
			}

			// Process the transaction through AML rules
			transaction = processTransaction(transaction);

			// If transaction is approved, update balances
			if (transaction.getStatus() == TransactionStatus.COMPLETED) {
				// Deduct from sender (original amount + conversion fee if applicable)
				senderAccount.setBalance(senderAccount.getBalance().subtract(totalDeductionFromSender));
				// Add to receiver (converted amount)
				receiverAccount.setBalance(receiverAccount.getBalance().add(finalAmount));

				accountRepository.save(senderAccount);
				accountRepository.save(receiverAccount);

				auditService.logSuccess(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION,
						transaction.getTransactionId(), userId, null,
						"Transfer completed: " + transferRequest.getAmount() + " " + transferRequest.getCurrency(),
						ipAddress);
			} else {
				auditService.logAction(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION,
						transaction.getTransactionId(), userId, null,
						"Transfer " + transaction.getStatus().toString().toLowerCase() + ": "
								+ transferRequest.getAmount() + " " + transferRequest.getCurrency(),
						ipAddress, userAgent, AuditStatus.PENDING);
			}

			return transaction;

		} catch (Exception e) {
			auditService.logFailure(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, null, userId, null,
					"Transfer failed: " + e.getMessage(), ipAddress);
			throw e;
		}
	}

	@Override
	public Transaction depositFunds(DepositRequest depositRequest, Long userId, String ipAddress, String userAgent) {
		try {
			// Find the account
			Account account = accountRepository.findByAccountNumber(depositRequest.getAccountNumber());
			if (account == null) {
				auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, userId,
						null, "Deposit failed - account not found: " + depositRequest.getAccountNumber(), ipAddress);
				throw new UserApiException("Account not found");
			}

			// SECURITY CHECK: Verify that the logged-in user owns the account
			if (!account.getCustomer().getUserId().equals(userId)) {
				auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, userId,
						null, "Unauthorized deposit attempt - user does not own account: "
								+ depositRequest.getAccountNumber(),
						ipAddress);
				throw new UserApiException("You are not authorized to deposit to this account");
			}

			// Check currency match
			if (!account.getCurrency().equals(depositRequest.getCurrency())) {
				auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, userId,
						null, "Deposit failed - currency mismatch", ipAddress);
				throw new UserApiException("Currency mismatch");
			}

			// Create deposit transaction
			Transaction transaction = new Transaction();
			transaction.setCustomer(account.getCustomer());
			transaction.setSenderAccount(null); // External deposit
			transaction.setReceiverAccount(account);
			transaction.setAmount(depositRequest.getAmount());
			transaction.setCurrency(depositRequest.getCurrency());
			transaction.setDescription(
					depositRequest.getDescription() + " (Source: " + depositRequest.getSourceOfFunds() + ")");
			transaction.setTransactionType(TransactionType.CREDIT);
			transaction.setCountryCode(depositRequest.getCountryCode());
			transaction.setCounterpartyName("External Deposit");

			// Process through AML rules
			transaction = processTransaction(transaction);

			// If approved, update balance
			if (transaction.getStatus() == TransactionStatus.COMPLETED) {
				account.setBalance(account.getBalance().add(depositRequest.getAmount()));
				accountRepository.save(account);

				auditService.logSuccess(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION,
						transaction.getTransactionId(), userId, null,
						"Deposit completed: " + depositRequest.getAmount() + " " + depositRequest.getCurrency(),
						ipAddress);
			} else {
				auditService.logAction(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION,
						transaction.getTransactionId(), userId, null,
						"Deposit " + transaction.getStatus().toString().toLowerCase() + ": "
								+ depositRequest.getAmount() + " " + depositRequest.getCurrency(),
						ipAddress, userAgent, AuditStatus.PENDING);
			}

			return transaction;

		} catch (Exception e) {
			auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, userId, null,
					"Deposit failed: " + e.getMessage(), ipAddress);
			throw e;
		}
	}

	@Override
	public Transaction withdrawFunds(WithdrawalRequest withdrawalRequest, Long userId, String ipAddress,
			String userAgent) {
		try {
			// Find the account
			Account account = accountRepository.findByAccountNumber(withdrawalRequest.getAccountNumber());
			if (account == null) {
				auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, userId,
						null, "Withdrawal failed - account not found: " + withdrawalRequest.getAccountNumber(),
						ipAddress);
				throw new UserApiException("Account not found");
			}

			// SECURITY CHECK: Verify that the logged-in user owns the account
			if (!account.getCustomer().getUserId().equals(userId)) {
				auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, userId,
						null, "Unauthorized withdrawal attempt - user does not own account: "
								+ withdrawalRequest.getAccountNumber(),
						ipAddress);
				throw new UserApiException("You are not authorized to withdraw from this account");
			}

			// Check sufficient balance
			if (account.getBalance().compareTo(withdrawalRequest.getAmount()) < 0) {
				auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, userId,
						null, "Withdrawal failed - insufficient balance", ipAddress);
				throw new UserApiException("Insufficient balance");
			}

			// Check currency match
			if (!account.getCurrency().equals(withdrawalRequest.getCurrency())) {
				auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, userId,
						null, "Withdrawal failed - currency mismatch", ipAddress);
				throw new UserApiException("Currency mismatch");
			}

			// Create withdrawal transaction
			Transaction transaction = new Transaction();
			transaction.setCustomer(account.getCustomer());
			transaction.setSenderAccount(account);
			transaction.setReceiverAccount(null); // External withdrawal
			transaction.setAmount(withdrawalRequest.getAmount());
			transaction.setCurrency(withdrawalRequest.getCurrency());
			transaction.setDescription(withdrawalRequest.getDescription() + " (Purpose: "
					+ withdrawalRequest.getPurposeOfWithdrawal() + ")");
			transaction.setTransactionType(TransactionType.DEBIT);
			transaction.setCountryCode(withdrawalRequest.getCountryCode());
			transaction.setCounterpartyName("External Withdrawal");

			// Process through AML rules
			transaction = processTransaction(transaction);

			// If approved, update balance
			if (transaction.getStatus() == TransactionStatus.COMPLETED) {
				account.setBalance(account.getBalance().subtract(withdrawalRequest.getAmount()));
				accountRepository.save(account);

				auditService
						.logSuccess(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION,
								transaction.getTransactionId(), userId, null, "Withdrawal completed: "
										+ withdrawalRequest.getAmount() + " " + withdrawalRequest.getCurrency(),
								ipAddress);
			} else {
				auditService.logAction(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION,
						transaction.getTransactionId(), userId, null,
						"Withdrawal " + transaction.getStatus().toString().toLowerCase() + ": "
								+ withdrawalRequest.getAmount() + " " + withdrawalRequest.getCurrency(),
						ipAddress, userAgent, AuditStatus.PENDING);
			}

			return transaction;

		} catch (Exception e) {
			auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, userId, null,
					"Withdrawal failed: " + e.getMessage(), ipAddress);
			throw e;
		}
	}

	// In TransactionServiceImpl.java
	public TransactionRepository getTransactionRepository() {
		return transactionRepository;
	}

	@Override
	public List<Transaction> getTransactionsByCustomerId(Long customerId) {
		return transactionRepository.findByCustomerUserIdOrderByCreatedAtDesc(customerId);
	}

	@Override
	public List<Transaction> getTransactionsByCustomerIdAndAccountNumber(Long customerId, String accountNumber) {
		return transactionRepository.findByCustomerUserIdAndAccountNumberOrderByCreatedAtDesc(customerId, accountNumber);
	}

	@Override
	public Transaction getTransactionByIdAndCustomerId(Long transactionId, Long customerId) {
		return transactionRepository.findByTransactionIdAndCustomerUserId(transactionId, customerId);
	}

	@Override
	public List<Transaction> getTransactionsByCustomerIdAndStatus(Long customerId, List<TransactionStatus> statuses) {
		return transactionRepository.findByCustomerUserIdAndStatusInOrderByCreatedAtDesc(customerId, statuses);
	}

	@Override
	public TransactionCountDto getTransactionCountsByCustomerId(Long customerId) {
		List<Transaction> transactions = transactionRepository.findByCustomerUserIdOrderByCreatedAtDesc(customerId);
		
		long totalCount = transactions.size();
		long completedCount = transactions.stream().filter(t -> t.getStatus() == TransactionStatus.COMPLETED).count();
		long pendingCount = transactions.stream().filter(t -> t.getStatus() == TransactionStatus.PENDING).count();
		long flaggedCount = transactions.stream().filter(t -> t.getStatus() == TransactionStatus.FLAGGED).count();
		long blockedCount = transactions.stream().filter(t -> t.getStatus() == TransactionStatus.BLOCKED).count();
		
		TransactionCountDto countDto = new TransactionCountDto();
		countDto.setTotalTransactions(totalCount);
		countDto.setCompletedTransactions(completedCount);
		countDto.setPendingTransactions(pendingCount);
		countDto.setFlaggedTransactions(flaggedCount);
		countDto.setBlockedTransactions(blockedCount);
		
		return countDto;
	}

	@Override
	public List<Transaction> getFlaggedTransactionsByCustomerId(Long customerId) {
		return transactionRepository.findByCustomerUserIdAndStatusInOrderByCreatedAtDesc(
			customerId, 
			List.of(TransactionStatus.FLAGGED)
		);
	}
}