package com.tss.aml.service;

import com.tss.aml.dto.request.DepositRequest;
import com.tss.aml.dto.request.TransferRequest;
import com.tss.aml.dto.request.WithdrawalRequest;
import com.tss.aml.entity.Transaction;

public interface TransactionService {
    Transaction processTransaction(Transaction transaction);
    Transaction transferFunds(TransferRequest transferRequest, Long userId, String ipAddress, String userAgent);
    Transaction depositFunds(DepositRequest depositRequest, Long userId, String ipAddress, String userAgent);
    Transaction withdrawFunds(WithdrawalRequest withdrawalRequest, Long userId, String ipAddress, String userAgent);
}