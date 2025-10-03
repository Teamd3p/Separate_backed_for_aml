package com.tss.aml.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tss.aml.dto.DepositRequest;
import com.tss.aml.dto.TransferRequest;
import com.tss.aml.dto.WithdrawalRequest;
import com.tss.aml.entity.Account;
import com.tss.aml.entity.AuditAction;
import com.tss.aml.entity.AuditResourceType;
import com.tss.aml.entity.AuditStatus;
import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.TransactionStatus;
import com.tss.aml.entity.TransactionType;
import com.tss.aml.exception.UserApiException;
import com.tss.aml.repository.AccountRepository;
import com.tss.aml.repository.TransactionRepository;
import com.tss.aml.rule.RuleEngineResult;
import com.tss.aml.service.AlertService;
import com.tss.aml.service.AuditService;
import com.tss.aml.service.RuleEngineService;
import com.tss.aml.service.TransactionService;
import com.tss.aml.util.AmlConfigProperties;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RuleEngineService ruleEngineService;

    @Autowired
    private AlertService alertService;
    
    @Autowired
    private AmlConfigProperties config;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private AuditService auditService;

    @Override
    public Transaction processTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatus.PENDING);
        transaction = transactionRepository.save(transaction);

        RuleEngineResult result = ruleEngineService.evaluate(transaction);

        if (!result.isSuspicious()) {
            transaction.setStatus(TransactionStatus.COMPLETED);
        } else if (result.getTotalRiskScore() >= 85) {
            transaction.setStatus(TransactionStatus.BLOCKED);
        } else {
            transaction.setStatus(TransactionStatus.FLAGGED);
        }

        transaction = transactionRepository.save(transaction);

        if (result.isSuspicious()) {
            alertService.createAlertForTransaction(transaction, result);
        }

        return transaction;
    }
    
    @Override
    public Transaction transferFunds(TransferRequest transferRequest, Long userId, String ipAddress, String userAgent) {
        try {
            // Find sender and receiver accounts
            Account senderAccount = accountRepository.findByAccountNumber(transferRequest.getSenderAccountNumber());
            if (senderAccount == null) {
                auditService.logFailure(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, null, 
                    userId, null, "Sender account not found: " + transferRequest.getSenderAccountNumber(), ipAddress);
                throw new UserApiException("Sender account not found");
            }
            
            Account receiverAccount = accountRepository.findByAccountNumber(transferRequest.getReceiverAccountNumber());
            if (receiverAccount == null) {
                auditService.logFailure(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, null, 
                    userId, null, "Receiver account not found: " + transferRequest.getReceiverAccountNumber(), ipAddress);
                throw new UserApiException("Receiver account not found");
            }
            
            // Check if sender has sufficient balance
            if (senderAccount.getBalance().compareTo(transferRequest.getAmount()) < 0) {
                auditService.logFailure(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, null, 
                    userId, null, "Insufficient balance in sender account", ipAddress);
                throw new UserApiException("Insufficient balance");
            }
            
            // Check currency match
            if (!senderAccount.getCurrency().equals(transferRequest.getCurrency()) || 
                !receiverAccount.getCurrency().equals(transferRequest.getCurrency())) {
                auditService.logFailure(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, null, 
                    userId, null, "Currency mismatch", ipAddress);
                throw new UserApiException("Currency mismatch");
            }
            
            // Create transaction
            Transaction transaction = new Transaction();
            transaction.setCustomer(senderAccount.getCustomer());
            transaction.setSenderAccount(senderAccount);
            transaction.setReceiverAccount(receiverAccount);
            transaction.setAmount(transferRequest.getAmount());
            transaction.setCurrency(transferRequest.getCurrency());
            transaction.setDescription(transferRequest.getDescription());
            transaction.setTransactionType(TransactionType.TRANSFER);
            transaction.setCounterpartyName(receiverAccount.getCustomer().getFirstName() + " " + receiverAccount.getCustomer().getLastName());
            transaction.setCounterpartyAccount(receiverAccount.getAccountNumber());
            
            // Process the transaction through AML rules
            transaction = processTransaction(transaction);
            
            // If transaction is approved, update balances
            if (transaction.getStatus() == TransactionStatus.COMPLETED) {
                senderAccount.setBalance(senderAccount.getBalance().subtract(transferRequest.getAmount()));
                receiverAccount.setBalance(receiverAccount.getBalance().add(transferRequest.getAmount()));
                
                accountRepository.save(senderAccount);
                accountRepository.save(receiverAccount);
                
                auditService.logSuccess(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, 
                    transaction.getTransactionId(), userId, null, 
                    "Transfer completed: " + transferRequest.getAmount() + " " + transferRequest.getCurrency(), ipAddress);
            } else {
                auditService.logAction(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, 
                    transaction.getTransactionId(), userId, null, 
                    "Transfer " + transaction.getStatus().toString().toLowerCase() + ": " + transferRequest.getAmount() + " " + transferRequest.getCurrency(), 
                    ipAddress, userAgent, AuditStatus.PENDING);
            }
            
            return transaction;
            
        } catch (Exception e) {
            auditService.logFailure(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, null, 
                userId, null, "Transfer failed: " + e.getMessage(), ipAddress);
            throw e;
        }
    }
    
    @Override
    public Transaction depositFunds(DepositRequest depositRequest, Long userId, String ipAddress, String userAgent) {
        try {
            // Find the account
            Account account = accountRepository.findByAccountNumber(depositRequest.getAccountNumber());
            if (account == null) {
                auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, 
                    userId, null, "Deposit failed - account not found: " + depositRequest.getAccountNumber(), ipAddress);
                throw new UserApiException("Account not found");
            }
            
            // Check currency match
            if (!account.getCurrency().equals(depositRequest.getCurrency())) {
                auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, 
                    userId, null, "Deposit failed - currency mismatch", ipAddress);
                throw new UserApiException("Currency mismatch");
            }
            
            // Create deposit transaction
            Transaction transaction = new Transaction();
            transaction.setCustomer(account.getCustomer());
            transaction.setSenderAccount(null); // External deposit
            transaction.setReceiverAccount(account);
            transaction.setAmount(depositRequest.getAmount());
            transaction.setCurrency(depositRequest.getCurrency());
            transaction.setDescription(depositRequest.getDescription() + " (Source: " + depositRequest.getSourceOfFunds() + ")");
            transaction.setTransactionType(TransactionType.CREDIT);
            transaction.setCounterpartyName("External Deposit");
            
            // Process through AML rules
            transaction = processTransaction(transaction);
            
            // If approved, update balance
            if (transaction.getStatus() == TransactionStatus.COMPLETED) {
                account.setBalance(account.getBalance().add(depositRequest.getAmount()));
                accountRepository.save(account);
                
                auditService.logSuccess(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, 
                    transaction.getTransactionId(), userId, null, 
                    "Deposit completed: " + depositRequest.getAmount() + " " + depositRequest.getCurrency(), ipAddress);
            } else {
                auditService.logAction(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, 
                    transaction.getTransactionId(), userId, null, 
                    "Deposit " + transaction.getStatus().toString().toLowerCase() + ": " + depositRequest.getAmount() + " " + depositRequest.getCurrency(), 
                    ipAddress, userAgent, AuditStatus.PENDING);
            }
            
            return transaction;
            
        } catch (Exception e) {
            auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, 
                userId, null, "Deposit failed: " + e.getMessage(), ipAddress);
            throw e;
        }
    }
    
    @Override
    public Transaction withdrawFunds(WithdrawalRequest withdrawalRequest, Long userId, String ipAddress, String userAgent) {
        try {
            // Find the account
            Account account = accountRepository.findByAccountNumber(withdrawalRequest.getAccountNumber());
            if (account == null) {
                auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, 
                    userId, null, "Withdrawal failed - account not found: " + withdrawalRequest.getAccountNumber(), ipAddress);
                throw new UserApiException("Account not found");
            }
            
            // Check sufficient balance
            if (account.getBalance().compareTo(withdrawalRequest.getAmount()) < 0) {
                auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, 
                    userId, null, "Withdrawal failed - insufficient balance", ipAddress);
                throw new UserApiException("Insufficient balance");
            }
            
            // Check currency match
            if (!account.getCurrency().equals(withdrawalRequest.getCurrency())) {
                auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, 
                    userId, null, "Withdrawal failed - currency mismatch", ipAddress);
                throw new UserApiException("Currency mismatch");
            }
            
            // Create withdrawal transaction
            Transaction transaction = new Transaction();
            transaction.setCustomer(account.getCustomer());
            transaction.setSenderAccount(account);
            transaction.setReceiverAccount(null); // External withdrawal
            transaction.setAmount(withdrawalRequest.getAmount());
            transaction.setCurrency(withdrawalRequest.getCurrency());
            transaction.setDescription(withdrawalRequest.getDescription() + " (Purpose: " + withdrawalRequest.getPurposeOfWithdrawal() + ")");
            transaction.setTransactionType(TransactionType.DEBIT);
            transaction.setCounterpartyName("External Withdrawal");
            
            // Process through AML rules
            transaction = processTransaction(transaction);
            
            // If approved, update balance
            if (transaction.getStatus() == TransactionStatus.COMPLETED) {
                account.setBalance(account.getBalance().subtract(withdrawalRequest.getAmount()));
                accountRepository.save(account);
                
                auditService.logSuccess(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, 
                    transaction.getTransactionId(), userId, null, 
                    "Withdrawal completed: " + withdrawalRequest.getAmount() + " " + withdrawalRequest.getCurrency(), ipAddress);
            } else {
                auditService.logAction(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, 
                    transaction.getTransactionId(), userId, null, 
                    "Withdrawal " + transaction.getStatus().toString().toLowerCase() + ": " + withdrawalRequest.getAmount() + " " + withdrawalRequest.getCurrency(), 
                    ipAddress, userAgent, AuditStatus.PENDING);
            }
            
            return transaction;
            
        } catch (Exception e) {
            auditService.logFailure(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, 
                userId, null, "Withdrawal failed: " + e.getMessage(), ipAddress);
            throw e;
        }
    }
    
    // In TransactionServiceImpl.java
    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }
}