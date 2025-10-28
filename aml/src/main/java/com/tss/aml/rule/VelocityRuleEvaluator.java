package com.tss.aml.rule;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.enums.TransactionType;
import com.tss.aml.repository.TransactionRepository;
import com.tss.aml.util.ObjectMapperHolder;
import com.tss.aml.util.RuleUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VelocityRuleEvaluator implements RuleEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(VelocityRuleEvaluator.class);

    private final TransactionRepository transactionRepository;

    @Override
    public boolean supports(String ruleType) {
        return "VELOCITY".equalsIgnoreCase(ruleType);
    }

    @Override
    public boolean evaluate(Transaction tx, Rule rule) {
        try {
            if (tx == null || tx.getCustomer() == null || tx.getAmount() == null) {
                return false;
            }

            Map<String, Object> cond = ObjectMapperHolder.readMap(rule.getConditions());
            Integer windowMinutes = RuleUtils.getInt(cond, "timeWindowMinutes");
            BigDecimal minAmount = RuleUtils.getBigDecimal(cond, "minAmount");
            Integer maxTransactions = RuleUtils.getInt(cond, "maxTransactions");

            if (windowMinutes == null || minAmount == null || maxTransactions == null) {
                logger.warn("Skipping VELOCITY rule {} due to missing conditions", rule.getName());
                return false;
            }

            // Ignore transactions below the minimum amount
            if (tx.getAmount().compareTo(minAmount) < 0) {
                logger.debug("✅ VELOCITY PASSED: {} | Amount below min", rule.getName());
                return false;
            }

            // Calculate cutoff time
            LocalDateTime cutoff = tx.getTimestamp().minusMinutes(windowMinutes);

            // Count recent qualifying transactions from DB
            long recentTxCount = transactionRepository
                    .countByCustomerUserIdAndTimestampAfterAndTransactionTypeAndAmountGreaterThanEqual(
                            tx.getCustomer().getUserId(),
                            cutoff,
                            TransactionType.CREDIT,
                            minAmount);

            // Include the current transaction in the count
            long totalTxCount = recentTxCount + 1;

            boolean triggered = totalTxCount > maxTransactions;

            if (triggered) {
                logger.warn("⚠️ VELOCITY TRIGGERED: {} | RecentTxCount={} | Threshold={}",
                        rule.getName(), totalTxCount, maxTransactions);
            } else {
                logger.debug("✅ VELOCITY PASSED: {} | RecentTxCount={} | Threshold={}",
                        rule.getName(), totalTxCount, maxTransactions);
            }

            return triggered;

        } catch (Exception e) {
            logger.error("❌ Error evaluating velocity rule {}: {}", rule.getName(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        return Optional.ofNullable(rule).map(Rule::getRiskScoreImpact).orElse(40);
    }
}
