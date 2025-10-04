package com.tss.aml.service;

import java.util.List;

import com.tss.aml.entity.Alert;
import com.tss.aml.entity.Transaction;
import com.tss.aml.rule.RuleEngineResult;

public interface AlertService {
    Alert createAlertForTransaction(Transaction transaction, RuleEngineResult result);
    Alert createAlert(Transaction transaction, List<String> triggeredRules, int riskScore);
}