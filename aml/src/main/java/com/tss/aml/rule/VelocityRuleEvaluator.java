package com.tss.aml.rule;

import java.math.BigDecimal;
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

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VelocityRuleEvaluator implements RuleEvaluator {

	private static final Logger logger = LoggerFactory.getLogger(VelocityRuleEvaluator.class);

	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public boolean supports(String ruleType) {
		return "VELOCITY".equalsIgnoreCase(ruleType);
	}

	@Override
	public boolean evaluate(Transaction tx, Rule rule) {
		try {
			if (tx == null || tx.getCustomer() == null)
				return false;
			Map<String, Object> cond = ObjectMapperHolder.readMap(rule.getConditions());
			Integer windowMinutes = RuleUtils.getInt(cond, "timeWindowMinutes");
			BigDecimal minAmount = RuleUtils.getBigDecimal(cond, "minAmount");
			if (windowMinutes == null || minAmount == null) {
				logger.warn("Skipping VELOCITY rule {} due to missing conditions", rule.getName());
				return false;
			}

			if (tx.getAmount() == null || tx.getAmount().compareTo(minAmount) < 0) {
				logger.debug("✅ VELOCITY PASSED: {} | Amount below min", rule.getName());
				return false;
			}

			LocalDateTime cutoff = tx.getTimestamp().minusMinutes(windowMinutes);
			long depositCount = transactionRepository
					.countByCustomerUserIdAndTimestampAfterAndTransactionTypeAndAmountGreaterThanEqual(
							tx.getCustomer().getUserId(), cutoff, com.tss.aml.entity.enums.TransactionType.CREDIT,
							minAmount);

			boolean triggered = depositCount > 0;
			if (triggered)
				logger.warn("⚠️ VELOCITY TRIGGERED: {} | recentDepositCount={}", rule.getName(), depositCount);
			else
				logger.debug("✅ VELOCITY PASSED: {}", rule.getName());
			return triggered;
		} catch (Exception e) {
			logger.error("❌ Error evaluating velocity rule {}: {}", rule.getName(), e.getMessage());
			return false;
		}
	}

	@Override
	public int getRiskScoreImpact(Rule rule) {
		return Optional.ofNullable(rule).map(Rule::getRiskScoreImpact).orElse(40);
	}
}