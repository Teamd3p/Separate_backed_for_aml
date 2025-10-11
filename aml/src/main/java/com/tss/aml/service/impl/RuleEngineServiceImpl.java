package com.tss.aml.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import com.tss.aml.repository.AlertRepository;
import com.tss.aml.repository.RuleRepository;
import com.tss.aml.rule.KycRuleEvaluator;
import com.tss.aml.rule.RuleEngineResult;
import com.tss.aml.rule.RuleEvaluator;
import com.tss.aml.service.RuleEngineService;

@Service
@Transactional
public class RuleEngineServiceImpl implements RuleEngineService {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineServiceImpl.class);

    private static final int BLOCK_THRESHOLD = 90;
    private static final int FLAG_THRESHOLD = 60;

    private final RuleRepository ruleRepository;
    private final List<RuleEvaluator> evaluators;
    private final KycRuleEvaluator kycRuleEvaluator;
    private final AlertRepository alertRepository;

    @Autowired
    public RuleEngineServiceImpl(RuleRepository ruleRepository,
                                 List<RuleEvaluator> evaluators,
                                 KycRuleEvaluator kycRuleEvaluator,
                                 AlertRepository alertRepository) {
        this.ruleRepository = ruleRepository;
        this.evaluators = evaluators;
        this.kycRuleEvaluator = kycRuleEvaluator;
        this.alertRepository = alertRepository;
    }
    @Override
    public RuleEngineResult evaluate(Transaction transaction) {
        logger.info("=== AML RULE EVALUATION START ===");
        logger.info("Transaction ID: {}, Amount: {} {}, Type: {}, Customer: {}",
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getTransactionType(),
                transaction.getCustomer() != null ? transaction.getCustomer().getUserId() : "Unknown");

        List<Rule> activeRules = ruleRepository.findByIsActiveTrue();
        logger.info("Found {} active rules to evaluate", activeRules.size());

        List<String> triggeredRules = new ArrayList<>();
        int totalTriggeredScore = 0;
        int totalMaxTriggeredScore = 0;

        // 1️⃣ Evaluate each active rule
        for (Rule rule : activeRules) {
            try {
                Optional<RuleEvaluator> evaluatorOpt = evaluators.stream()
                        .filter(ev -> ev.supports(rule.getType().name()))
                        .findFirst();

                if (evaluatorOpt.isEmpty()) {
                    logger.warn("Skipping rule '{}' (type {}) – no evaluator found", rule.getName(), rule.getType());
                    continue;
                }

                RuleEvaluator evaluator = evaluatorOpt.get();
                boolean triggered = evaluator.evaluate(transaction, rule);

                if (triggered) {
                    triggeredRules.add(rule.getName());
                    int riskImpact = evaluator.getRiskScoreImpact(rule);
                    totalTriggeredScore += riskImpact;
                    totalMaxTriggeredScore += riskImpact; // only triggered rules
                    logger.warn("🚨 RULE TRIGGERED: {} | Risk Impact: {} | Running Total: {}",
                            rule.getName(), riskImpact, totalTriggeredScore);
                } else {
                    totalMaxTriggeredScore += rule.getRiskScoreImpact(); // include for normalization
                    logger.debug("✅ Rule passed: {}", rule.getName());
                }

            } catch (Exception e) {
                logger.error("❌ Error evaluating rule {}: {}", rule.getName(), e.getMessage(), e);
            }
        }

        // 2️⃣ Evaluate KYC rules
        try {
            kycRuleEvaluator.evaluateKycRulesForTransaction(transaction);
            logger.info("KYC rule evaluation completed successfully");
        } catch (Exception e) {
            logger.error("Error during KYC rule evaluation: {}", e.getMessage(), e);
        }

        // 3️⃣ Normalize triggered score properly
        double normalizedRuleScore = totalMaxTriggeredScore == 0 ? 0 :
                ((double) totalTriggeredScore / totalMaxTriggeredScore) * 100;

        // 4️⃣ Amount factor
        BigDecimal amount = Optional.ofNullable(transaction.getAmount()).orElse(BigDecimal.ZERO);
        BigDecimal reference = new BigDecimal("1000000"); // 1M as reference
        BigDecimal factor = amount.divide(reference, 4, RoundingMode.HALF_UP);
        if (factor.compareTo(BigDecimal.ONE) > 0) factor = BigDecimal.ONE;
        double normalizedAmountScore = factor.doubleValue() * 100;

        // 5️⃣ Combine rule score and amount factor
        double combinedScore = normalizedRuleScore * 0.7 + normalizedAmountScore * 0.3;
        int finalScore = (int) Math.round(combinedScore);

        // 6️⃣ Determine action
        String action;
        boolean suspicious;
        if (finalScore >= BLOCK_THRESHOLD) {
            action = "BLOCK";
            suspicious = true;
        } else if (finalScore >= FLAG_THRESHOLD) {
            action = "FLAG";
            suspicious = true;
        } else {
            action = "ALLOW";
            suspicious = false;
        }

        // 7️⃣ Log summary
        logger.info("=== EVALUATION SUMMARY ===");
        logger.info("Final Risk Score: {}/100", finalScore);
        logger.info("Triggered Rules: {}", triggeredRules);
        logger.info("Transaction Action: {}", action);
        logger.info("=== AML RULE EVALUATION END ===");

        return suspicious ?
                RuleEngineResult.suspicious(finalScore, triggeredRules) :
                RuleEngineResult.clean(finalScore, triggeredRules);
    }

}
