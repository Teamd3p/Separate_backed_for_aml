package com.tss.aml.rule;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import com.tss.aml.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class FrequencyRuleEvaluator implements RuleEvaluator {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public boolean supports(String ruleType) {
        return "FREQUENCY".equals(ruleType);
    }

    @Override
    public boolean evaluate(Transaction tx, Rule rule) {
        try {
            Map<String, Object> cond = com.tss.aml.util.ObjectMapperHolder.readMap(rule.getConditions());
            int maxCount = ((Number) cond.get("maxTransactions")).intValue();
            int timeWindowMinutes = ((Number) cond.get("timeWindowMinutes")).intValue();

            LocalDateTime cutoff = tx.getTimestamp().minusMinutes(timeWindowMinutes);
            long count = transactionRepository.countByCustomerUserIdAndTimestampAfter(
            	    tx.getCustomer().getUserId(), cutoff
            	);
            return count >= maxCount;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        return rule.getRiskScoreImpact();
    }
}