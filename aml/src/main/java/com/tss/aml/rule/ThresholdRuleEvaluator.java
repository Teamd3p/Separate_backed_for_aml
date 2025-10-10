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
			Map<String, Object> cond = ObjectMapperHolder.readMap(rule.getConditions());
			BigDecimal threshold = RuleUtils.getBigDecimal(cond, "amountThreshold");
			String currency = RuleUtils.getString(cond, "currency");
			if (threshold == null || currency == null) {
				logger.warn("Skipping THRESHOLD rule {} due to missing condition keys", rule.getName());
				return false;
			}
			if (tx.getAmount() == null || tx.getCurrency() == null)
				return false;

			boolean triggered = tx.getCurrency().equalsIgnoreCase(currency) && tx.getAmount().compareTo(threshold) >= 0;
			if (triggered)
				logger.info("⚠️ THRESHOLD TRIGGERED: {} | {} {} >= {}", rule.getName(), tx.getAmount(),
						tx.getCurrency(), threshold);
			else
				logger.debug("✅ THRESHOLD PASSED: {}", rule.getName());
			return triggered;
		} catch (Exception e) {
			logger.error("❌ Error evaluating threshold rule {}: {}", rule.getName(), e.getMessage());
			return false;
		}
	}

	@Override
	public int getRiskScoreImpact(Rule rule) {
		return Optional.ofNullable(rule).map(Rule::getRiskScoreImpact).orElse(50);
	}
}