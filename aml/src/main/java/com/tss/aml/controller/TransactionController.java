package com.tss.aml.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.tss.aml.dto.DepositRequest;
import com.tss.aml.dto.TransactionResponse;
import com.tss.aml.dto.TransferRequest;
import com.tss.aml.dto.WithdrawalRequest;
import com.tss.aml.entity.AuditAction;
import com.tss.aml.entity.AuditResourceType;
import com.tss.aml.entity.Transaction;
import com.tss.aml.repository.TransactionRepository;
import com.tss.aml.service.AuditService;
import com.tss.aml.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AuditService auditService;
    
    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transferFunds(
            @Valid @RequestBody TransferRequest transferRequest,
            @RequestParam Long userId,
            HttpServletRequest request) {
        
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        // Log the transfer attempt
        auditService.logAction(AuditAction.TRANSFER_FUNDS, AuditResourceType.TRANSACTION, null, 
            userId, null, "Transfer attempt initiated", ipAddress, userAgent);
        
        Transaction transaction = transactionService.transferFunds(transferRequest, userId, ipAddress, userAgent);
        
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setSenderAccountNumber(transaction.getSenderAccount() != null ? transaction.getSenderAccount().getAccountNumber() : null);
        response.setReceiverAccountNumber(transaction.getReceiverAccount() != null ? transaction.getReceiverAccount().getAccountNumber() : null);
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency());
        response.setDescription(transaction.getDescription());
        response.setTransactionType(transaction.getTransactionType());
        response.setStatus(transaction.getStatus());
        response.setTimestamp(transaction.getTimestamp());
        response.setCounterpartyName(transaction.getCounterpartyName());
        response.setCounterpartyAccount(transaction.getCounterpartyAccount());
        response.setCountryCode(transaction.getCountryCode());
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> depositFunds(
            @Valid @RequestBody DepositRequest depositRequest,
            @RequestParam Long userId,
            HttpServletRequest request) {
        
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        // Log the deposit attempt
        auditService.logAction(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, 
            userId, null, "Deposit attempt initiated", ipAddress, userAgent);
        
        Transaction transaction = transactionService.depositFunds(depositRequest, userId, ipAddress, userAgent);
        
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setSenderAccountNumber(transaction.getSenderAccount() != null ? transaction.getSenderAccount().getAccountNumber() : null);
        response.setReceiverAccountNumber(transaction.getReceiverAccount() != null ? transaction.getReceiverAccount().getAccountNumber() : null);
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency());
        response.setDescription(transaction.getDescription());
        response.setTransactionType(transaction.getTransactionType());
        response.setStatus(transaction.getStatus());
        response.setTimestamp(transaction.getTimestamp());
        response.setCounterpartyName(transaction.getCounterpartyName());
        response.setCounterpartyAccount(transaction.getCounterpartyAccount());
        response.setCountryCode(transaction.getCountryCode());
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdrawFunds(
            @Valid @RequestBody WithdrawalRequest withdrawalRequest,
            @RequestParam Long userId,
            HttpServletRequest request) {
        
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        // Log the withdrawal attempt
        auditService.logAction(AuditAction.TRANSACTION_CREATED, AuditResourceType.TRANSACTION, null, 
            userId, null, "Withdrawal attempt initiated", ipAddress, userAgent);
        
        Transaction transaction = transactionService.withdrawFunds(withdrawalRequest, userId, ipAddress, userAgent);
        
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setSenderAccountNumber(transaction.getSenderAccount() != null ? transaction.getSenderAccount().getAccountNumber() : null);
        response.setReceiverAccountNumber(transaction.getReceiverAccount() != null ? transaction.getReceiverAccount().getAccountNumber() : null);
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency());
        response.setDescription(transaction.getDescription());
        response.setTransactionType(transaction.getTransactionType());
        response.setStatus(transaction.getStatus());
        response.setTimestamp(transaction.getTimestamp());
        response.setCounterpartyName(transaction.getCounterpartyName());
        response.setCounterpartyAccount(transaction.getCounterpartyAccount());
        response.setCountryCode(transaction.getCountryCode());
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            @PathVariable String accountNumber,
            HttpServletRequest request) {
        
        String ipAddress = getClientIpAddress(request);
        
        // Find transactions where this account is either sender or receiver
        List<Transaction> transactions = transactionRepository.findBySenderAccountAccountNumberOrReceiverAccountAccountNumber(
            accountNumber, accountNumber);
        
        List<TransactionResponse> responses = transactions.stream()
            .map(transaction -> {
                TransactionResponse response = new TransactionResponse();
                response.setTransactionId(transaction.getTransactionId());
                response.setSenderAccountNumber(transaction.getSenderAccount() != null ? transaction.getSenderAccount().getAccountNumber() : null);
                response.setReceiverAccountNumber(transaction.getReceiverAccount() != null ? transaction.getReceiverAccount().getAccountNumber() : null);
                response.setAmount(transaction.getAmount());
                response.setCurrency(transaction.getCurrency());
                response.setDescription(transaction.getDescription());
                response.setTransactionType(transaction.getTransactionType());
                response.setStatus(transaction.getStatus());
                response.setTimestamp(transaction.getTimestamp());
                response.setCounterpartyName(transaction.getCounterpartyName());
                response.setCounterpartyAccount(transaction.getCounterpartyAccount());
                response.setCountryCode(transaction.getCountryCode());
                return response;
            })
            .collect(Collectors.toList());
        
        auditService.logSuccess(AuditAction.DATA_VIEWED, AuditResourceType.TRANSACTION, null, 
            null, null, "Transaction history queried for account: " + accountNumber, ipAddress);
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long transactionId,
            HttpServletRequest request) {
        
        String ipAddress = getClientIpAddress(request);
        
        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);
        if (transaction == null) {
            auditService.logFailure(AuditAction.DATA_VIEWED, AuditResourceType.TRANSACTION, transactionId, 
                null, null, "Transaction not found: " + transactionId, ipAddress);
            return ResponseEntity.notFound().build();
        }
        
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setSenderAccountNumber(transaction.getSenderAccount() != null ? transaction.getSenderAccount().getAccountNumber() : null);
        response.setReceiverAccountNumber(transaction.getReceiverAccount() != null ? transaction.getReceiverAccount().getAccountNumber() : null);
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency());
        response.setDescription(transaction.getDescription());
        response.setTransactionType(transaction.getTransactionType());
        response.setStatus(transaction.getStatus());
        response.setTimestamp(transaction.getTimestamp());
        response.setCounterpartyName(transaction.getCounterpartyName());
        response.setCounterpartyAccount(transaction.getCounterpartyAccount());
        response.setCountryCode(transaction.getCountryCode());
        
        auditService.logSuccess(AuditAction.DATA_VIEWED, AuditResourceType.TRANSACTION, transactionId, 
            null, null, "Transaction details viewed: " + transactionId, ipAddress);
        
        return ResponseEntity.ok(response);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
