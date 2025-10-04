package com.tss.aml.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.aml.entity.Alert;
import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.enums.AlertStatus;
import com.tss.aml.repository.AlertRepository;
import com.tss.aml.rule.RuleEngineResult;
import com.tss.aml.service.AlertService;

@Service
@Transactional
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Override
    public Alert createAlertForTransaction(Transaction transaction, RuleEngineResult result) {
        Alert alert = new Alert();
        alert.setCustomer(transaction.getCustomer());
        alert.setTransaction(transaction);
        alert.setRuleTriggered(String.join(", ", result.getTriggeredRuleNames()));
        alert.setRiskScore(result.getTotalRiskScore());
        alert.setStatus(AlertStatus.PENDING); // Awaiting investigation
        // assignedTo remains null until assigned by system or admin

        return alertRepository.save(alert);
    }

    @Override
    public Alert createAlert(Transaction transaction, List<String> triggeredRules, int riskScore) {
        Alert alert = new Alert();
        alert.setCustomer(transaction.getCustomer());
        alert.setTransaction(transaction);
        alert.setRuleTriggered(String.join(", ", triggeredRules));
        alert.setRiskScore(riskScore);
        alert.setStatus(AlertStatus.PENDING); // Awaiting investigation
        // assignedTo remains null until assigned by system or admin

        return alertRepository.save(alert);
    }
}