package com.tss.aml.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.TransactionStatus;
import com.tss.aml.repository.TransactionRepository;
import com.tss.aml.rule.RuleEngineResult;
import com.tss.aml.service.AlertService;
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

    @Override
    public Transaction processTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatus.PENDING);
        transaction = transactionRepository.save(transaction);

        RuleEngineResult result = ruleEngineService.evaluate(transaction);

        if (!result.isSuspicious()) {
            transaction.setStatus(TransactionStatus.COMPLETED);
        } else if (result.getTotalRiskScore() >= 70) {
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
    
 // In TransactionServiceImpl.java
    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }
}