package com.tss.aml.rule;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import com.tss.aml.repository.TransactionRepository;
import com.tss.aml.util.ObjectMapperHolder;
import com.tss.aml.util.RuleUtils;

@Component
public class FrequencyRuleEvaluator implements RuleEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(FrequencyRuleEvaluator.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public boolean supports(String ruleType) {
        return "FREQUENCY".equalsIgnoreCase(ruleType);
    }

    @Override
    public boolean evaluate(Transaction tx, Rule rule) {
        try {
            if (tx == null || tx.getCustomer() == null)
                return false;

            Map<String, Object> cond = ObjectMapperHolder.readMap(rule.getConditions());
            Integer maxCount = RuleUtils.getInt(cond, "maxTransactions");
            Integer windowMinutes = RuleUtils.getInt(cond, "timeWindowMinutes");

            if (maxCount == null || windowMinutes == null) {
                logger.warn("Skipping FREQUENCY rule {} due to missing conditions", rule.getName());
                return false;
            }

            LocalDateTime cutoff = tx.getTimestamp().minusMinutes(windowMinutes);
            long count = transactionRepository.countByCustomerUserIdAndTimestampAfter(
                tx.getCustomer().getUserId(), cutoff);

            boolean triggered = count >= maxCount;
            if (triggered) {
                logger.warn("⚠️ FREQUENCY TRIGGERED: {} | count={} >= {}", rule.getName(), count, maxCount);
            } else {
                logger.debug("✅ FREQUENCY PASSED: {} | count={}", rule.getName(), count);
            }
            return triggered;

        } catch (Exception e) {
            logger.error("❌ Error evaluating frequency rule {}: {}", rule.getName(), e.getMessage());
            return false;
        }
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        // Default base impact if conditions are missing
        int baseImpact = Optional.ofNullable(rule).map(Rule::getRiskScoreImpact).orElse(60);
        
        // We cannot access the actual transaction count here because this method is called
        // outside the evaluate() context.
        // So we return the base impact — the real scaling will happen in RuleEngineServiceImpl.
        return baseImpact;
    }

    // 👇 NEW METHOD: calculate scaled risk based on actual count
    public int calculateScaledRiskScore(Rule rule, long actualCount) {
        if (rule == null || actualCount <= 0) {
            return 0;
        }

        Map<String, Object> cond = null;
		try {
			cond = ObjectMapperHolder.readMap(rule.getConditions());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Integer maxCount = RuleUtils.getInt(cond, "maxTransactions");
        int baseImpact = rule.getRiskScoreImpact();

        if (maxCount == null || maxCount <= 0) {
            return baseImpact; // fallback
        }

        // Scale: at maxCount → baseImpact, at 2*maxCount → 1.5x, capped at 100
        double multiplier = 1.0 + (Math.max(0, actualCount - maxCount) * 0.1);
        int scaled = (int) Math.round(baseImpact * multiplier);
        return Math.min(100, scaled);
    }
}