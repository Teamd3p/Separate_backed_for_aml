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
public class FunnelAccountRuleEvaluator implements RuleEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(FunnelAccountRuleEvaluator.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public boolean supports(String ruleType) {
        return "FUNNEL_ACCOUNT".equals(ruleType);
    }

    @Override
    public boolean evaluate(Transaction tx, Rule rule) {
        // Use counterpartyAccount as receiver ID
        String receiverId = tx.getCounterpartyAccount();
        if (receiverId == null || receiverId.trim().isEmpty()) {
            logger.debug("No counterpartyAccount – skipping funnel rule");
            return false;
        }

        Map<String, Object> cond = null;
		try {
			cond = ObjectMapperHolder.readMap(rule.getConditions());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Integer minSenders = RuleUtils.getInt(cond, "minSenders");      // e.g., 5
        Integer windowMinutes = RuleUtils.getInt(cond, "timeWindowMinutes"); // e.g., 60

        if (minSenders == null || windowMinutes == null) {
            logger.warn("Missing conditions in FUNNEL_ACCOUNT rule");
            return false;
        }

        LocalDateTime cutoff = tx.getTimestamp().minusMinutes(windowMinutes);

        // Count distinct senders (customer.userIds) sending to this receiverId in time window
        long uniqueSenders = transactionRepository.countDistinctSendersToReceiverAfter(
            receiverId, cutoff
        );

        boolean triggered = uniqueSenders >= minSenders;
        if (triggered) {
            logger.warn("⚠️ FUNNEL ACCOUNT DETECTED: {}+ unique senders to receiver {} in {} minutes",
                uniqueSenders, receiverId, windowMinutes);
        }
        return triggered;
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        return Optional.ofNullable(rule).map(Rule::getRiskScoreImpact).orElse(75);
    }
}