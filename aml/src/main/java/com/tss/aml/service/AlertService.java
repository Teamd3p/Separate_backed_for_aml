package com.tss.aml.service;

import com.tss.aml.entity.Alert;
import com.tss.aml.entity.Transaction;
import com.tss.aml.rule.RuleEngineResult;

public interface AlertService {
    Alert createAlertForTransaction(Transaction transaction, RuleEngineResult result);
}