package com.tss.aml.service;

import com.tss.aml.entity.Transaction;

public interface TransactionService {
    Transaction processTransaction(Transaction transaction);
}