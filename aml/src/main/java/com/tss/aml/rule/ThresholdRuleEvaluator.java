package com.tss.aml.rule;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;

@Component
public class ThresholdRuleEvaluator implements RuleEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(ThresholdRuleEvaluator.class);

    @Override
    public boolean supports(String ruleType) {
        return "THRESHOLD".equals(ruleType);
    }

    @Override
    public boolean evaluate(Transaction tx, Rule rule) {
        try {
            Map<String, Object> cond = com.tss.aml.util.ObjectMapperHolder.readMap(rule.getConditions());
            BigDecimal threshold = new BigDecimal(cond.get("amountThreshold").toString());
            String currency = (String) cond.get("currency");

            logger.debug("🔍 THRESHOLD Rule: {} | Checking amount {} {} against threshold {} {}", 
                rule.getName(), tx.getAmount(), tx.getCurrency(), threshold, currency);

            boolean triggered = tx.getCurrency().equalsIgnoreCase(currency) &&
                               tx.getAmount().compareTo(threshold) >= 0;

            if (triggered) {
                logger.info("⚠️ THRESHOLD TRIGGERED: {} | Amount {} {} exceeds threshold {} {}", 
                    rule.getName(), tx.getAmount(), tx.getCurrency(), threshold, currency);
            } else {
                logger.debug("✅ THRESHOLD PASSED: {} | Amount {} {} below threshold {} {}", 
                    rule.getName(), tx.getAmount(), tx.getCurrency(), threshold, currency);
            }

            return triggered;
        } catch (Exception e) {
            logger.error("❌ Error evaluating threshold rule {}: {}", rule.getName(), e.getMessage());
            return false; // Fail-safe
        }
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        return rule.getRiskScoreImpact();
    }
}