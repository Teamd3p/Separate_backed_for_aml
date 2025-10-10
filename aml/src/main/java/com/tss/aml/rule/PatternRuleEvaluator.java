package com.tss.aml.rule;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import com.tss.aml.util.ObjectMapperHolder;
import com.tss.aml.util.RuleUtils;

import java.util.regex.Pattern;

@Component
public class PatternRuleEvaluator implements RuleEvaluator {

	private static final Logger logger = LoggerFactory.getLogger(PatternRuleEvaluator.class);

	@Override
	public boolean supports(String ruleType) {
		return "PATTERN".equalsIgnoreCase(ruleType);
	}

	@Override
	public boolean evaluate(Transaction tx, Rule rule) {
		try {
			if (tx == null)
				return false;
			Map<String, Object> cond = ObjectMapperHolder.readMap(rule.getConditions());
			String regex = RuleUtils.getString(cond, "regex");
			if (regex == null || regex.trim().isEmpty()) {
				logger.warn("Skipping PATTERN rule {} due to missing regex", rule.getName());
				return false;
			}
			String text = Optional.ofNullable(tx.getDescription()).orElse("");
			boolean triggered = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text).find();
			if (triggered)
				logger.warn("⚠️ PATTERN TRIGGERED: {} | regex={}", rule.getName(), regex);
			else
				logger.debug("✅ PATTERN PASSED: {}", rule.getName());
			return triggered;
		} catch (Exception e) {
			logger.error("❌ Error evaluating pattern rule {}: {}", rule.getName(), e.getMessage());
			return false;
		}
	}

	@Override
	public int getRiskScoreImpact(Rule rule) {
		return Optional.ofNullable(rule).map(Rule::getRiskScoreImpact).orElse(40);
	}
}