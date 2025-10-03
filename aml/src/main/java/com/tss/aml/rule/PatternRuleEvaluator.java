package com.tss.aml.rule;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import com.tss.aml.repository.TransactionRepository;

@Component
public class PatternRuleEvaluator implements RuleEvaluator {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public boolean supports(String ruleType) {
        return "PATTERN".equals(ruleType);
    }

    @Override
    public boolean evaluate(Transaction tx, Rule rule) {
        try {
            Map<String, Object> cond = com.tss.aml.util.ObjectMapperHolder.readMap(rule.getConditions());
            BigDecimal belowThreshold = new BigDecimal(cond.get("belowAmount").toString());
            int minCount = ((Number) cond.get("minTransactions")).intValue();
            int timeWindowHours = ((Number) cond.get("timeWindowHours")).intValue();
            
            
            // Check if this tx is below threshold
            if (tx.getAmount().compareTo(belowThreshold) >= 0) return false;

            LocalDateTime cutoff = tx.getTimestamp().minusHours(timeWindowHours);
            List<BigDecimal> amounts = transactionRepository
                .findAmountsByCustomerIdAndTimestampAfterAndAmountLessThan(
                    tx.getCustomer().getUserId(), cutoff, belowThreshold
                );

            return amounts.size() >= minCount;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        return rule.getRiskScoreImpact();
    }
}