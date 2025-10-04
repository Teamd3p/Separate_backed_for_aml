package com.tss.aml.rule;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import com.tss.aml.repository.TransactionRepository;

@Component
public class FrequencyRuleEvaluator implements RuleEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(FrequencyRuleEvaluator.class);

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

            logger.debug("🔍 FREQUENCY Rule: {} | Customer {} has {} transactions in last {} minutes (max: {})", 
                rule.getName(), tx.getCustomer().getUserId(), count, timeWindowMinutes, maxCount);

            boolean triggered = count >= maxCount;

            if (triggered) {
                logger.warn("⚠️ FREQUENCY TRIGGERED: {} | Customer {} exceeded limit: {} >= {} transactions in {} minutes", 
                    rule.getName(), tx.getCustomer().getUserId(), count, maxCount, timeWindowMinutes);
            } else {
                logger.debug("✅ FREQUENCY PASSED: {} | Customer {} within limit: {} < {} transactions in {} minutes", 
                    rule.getName(), tx.getCustomer().getUserId(), count, maxCount, timeWindowMinutes);
            }

            return triggered;
        } catch (Exception e) {
            logger.error("❌ Error evaluating frequency rule {}: {}", rule.getName(), e.getMessage());
            return false;
        }
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        return rule.getRiskScoreImpact();
    }
}