package com.tss.aml.rule;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ThresholdRuleEvaluator implements RuleEvaluator {

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

            return tx.getCurrency().equalsIgnoreCase(currency) &&
                   tx.getAmount().compareTo(threshold) >= 0;
        } catch (Exception e) {
            return false; // Fail-safe
        }
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        return rule.getRiskScoreImpact();
    }
}