package com.tss.aml.rule;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import com.tss.aml.util.ObjectMapperHolder;
import com.tss.aml.util.RuleUtils;

@Component
public class ThresholdRuleEvaluator implements RuleEvaluator {

	private static final Logger logger = LoggerFactory.getLogger(ThresholdRuleEvaluator.class);

	@Override
	public boolean supports(String ruleType) {
		return "THRESHOLD".equalsIgnoreCase(ruleType);
	}

	@Override
	public boolean evaluate(Transaction tx, Rule rule) {
		try {
			logger.info("💰 THRESHOLD RULE EVALUATION - Rule: {}", rule.getName());
			
			// Debug rule conditions
			Map<String, Object> cond = ObjectMapperHolder.readMap(rule.getConditions());
			logger.info("💰 Rule conditions: {}", cond);
			
			// Try multiple field name formats for threshold
			BigDecimal threshold = RuleUtils.getBigDecimal(cond, "amountThreshold");
			BigDecimal minAmount = RuleUtils.getBigDecimal(cond, "minAmount");
			BigDecimal maxAmount = RuleUtils.getBigDecimal(cond, "maxAmount");
			
			// For range-based rules (like structuring), use minAmount as threshold
			if (threshold == null && minAmount != null) {
				threshold = minAmount;
			}
			if (threshold == null && maxAmount != null) {
				threshold = maxAmount;
			}
			
			// Currency is optional - if not specified, apply to all currencies
			String currency = RuleUtils.getString(cond, "currency");
			if (currency == null || currency.trim().isEmpty()) {
				currency = "ANY"; // Apply to any currency
			}
			
			logger.info("💰 Parsed threshold: {} {}", threshold, currency);
			
			if (threshold == null) {
				logger.warn("💰 Skipping THRESHOLD rule {} - no valid threshold found in conditions: {}", 
						rule.getName(), cond);
				return false;
			}
			
			// Debug transaction fields
			logger.info("💰 Transaction amount: {} {}", tx.getAmount(), tx.getCurrency());
			
			if (tx.getAmount() == null || tx.getCurrency() == null) {
				logger.warn("💰 Transaction missing required fields - amount: {}, currency: {}", 
						tx.getAmount(), tx.getCurrency());
				return false;
			}

			// Check threshold - handle both simple threshold and range-based rules
			boolean currencyMatches = "ANY".equals(currency) || tx.getCurrency().equalsIgnoreCase(currency);
			boolean triggered = false;
			
			if (currencyMatches) {
				if (minAmount != null && maxAmount != null) {
					// Range-based rule (e.g., structuring detection)
					triggered = tx.getAmount().compareTo(minAmount) >= 0 && tx.getAmount().compareTo(maxAmount) <= 0;
					if (triggered) {
						logger.warn("⚠️ THRESHOLD RULE TRIGGERED: {} | {} {} is between {} and {} (currency: {})", 
								rule.getName(), tx.getAmount(), tx.getCurrency(), minAmount, maxAmount, currency);
					} else {
						logger.info("✅ THRESHOLD RULE PASSED: {} | {} {} not in range [{}, {}]", 
								rule.getName(), tx.getAmount(), tx.getCurrency(), minAmount, maxAmount);
					}
				} else {
					// Simple threshold rule
					triggered = tx.getAmount().compareTo(threshold) >= 0;
					if (triggered) {
						logger.warn("⚠️ THRESHOLD RULE TRIGGERED: {} | {} {} >= {} (currency: {})", 
								rule.getName(), tx.getAmount(), tx.getCurrency(), threshold, currency);
					} else {
						logger.info("✅ THRESHOLD RULE PASSED: {} | {} {} < {}", 
								rule.getName(), tx.getAmount(), tx.getCurrency(), threshold);
					}
				}
			} else {
				logger.info("✅ THRESHOLD RULE PASSED: {} | Currency mismatch - tx: {}, rule: {}", 
						rule.getName(), tx.getCurrency(), currency);
			}
			
			return triggered;
		} catch (Exception e) {
			logger.error("❌ Error evaluating threshold rule {}: {}", rule.getName(), e.getMessage(), e);
			return false;
		}
	}

	@Override
	public int getRiskScoreImpact(Rule rule) {
		return Optional.ofNullable(rule).map(Rule::getRiskScoreImpact).orElse(50);
	}
}