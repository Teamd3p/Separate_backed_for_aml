package com.tss.aml.rule;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.enums.TransactionType;
import com.tss.aml.repository.TransactionRepository;
import com.tss.aml.util.ObjectMapperHolder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VelocityRuleEvaluator implements RuleEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(VelocityRuleEvaluator.class);
    private final TransactionRepository transactionRepository;

    @Override
    public boolean supports(String ruleType) {
        return "VELOCITY".equals(ruleType);
    }

    @Override
    public boolean evaluate(Transaction tx, Rule rule) {
        if (!tx.getTransactionType().equals(TransactionType.TRANSFER)) {
            logger.debug("✅ VELOCITY PASSED: {} | Not a transfer transaction", rule.getName());
            return false;
        }

        try {
            Map<String, Object> cond = ObjectMapperHolder.readMap(rule.getConditions());
            int timeWindowMinutes = ((Number) cond.get("timeWindowMinutes")).intValue();
            BigDecimal minAmount = new BigDecimal(cond.get("minAmount").toString());

            if (tx.getAmount().compareTo(minAmount) < 0) {
                logger.debug("✅ VELOCITY PASSED: {} | Amount {} below minimum {}", 
                    rule.getName(), tx.getAmount(), minAmount);
                return false;
            }

            LocalDateTime cutoff = tx.getTimestamp().minusMinutes(timeWindowMinutes);
            // Find recent CREDIT (deposit) transactions
            long depositCount = transactionRepository.countByCustomerUserIdAndTimestampAfterAndTransactionTypeAndAmountGreaterThanEqual(
                tx.getCustomer().getUserId(), 
                cutoff, 
                TransactionType.CREDIT, 
                minAmount
            );

            logger.debug("🔍 VELOCITY Rule: {} | Customer {} has {} recent deposits >= {} in last {} minutes", 
                rule.getName(), tx.getCustomer().getUserId(), depositCount, minAmount, timeWindowMinutes);

            boolean triggered = depositCount > 0;
            if (triggered) {
                logger.warn("⚠️ VELOCITY TRIGGERED: {} | Customer {} has {} recent large deposits before transfer", 
                    rule.getName(), tx.getCustomer().getUserId(), depositCount);
            } else {
                logger.debug("✅ VELOCITY PASSED: {} | No recent large deposits found", rule.getName());
            }

            return triggered;
        } catch (Exception e) {
            logger.error("❌ Error evaluating velocity rule {}: {}", rule.getName(), e.getMessage());
            return false;
        }
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        return rule.getRiskScoreImpact();
    }
}