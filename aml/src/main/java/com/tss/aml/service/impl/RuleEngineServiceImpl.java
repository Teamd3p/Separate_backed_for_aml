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
    public RuleEngineServiceImpl(RuleRepository ruleRepository, List<RuleEvaluator> evaluators,
            KycRuleEvaluator kycRuleEvaluator, AlertRepository alertRepository) {
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
        int rawRiskScore = 0;

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
                    rawRiskScore += riskImpact;
                    logger.warn("🚨 RULE TRIGGERED: {} | Risk Impact: {} | Running Total: {}", 
                            rule.getName(), riskImpact, rawRiskScore);
                } else {
                    logger.debug("✅ Rule passed: {}", rule.getName());
                }

            } catch (Exception e) {
                logger.error("❌ Error evaluating rule {}: {}", rule.getName(), e.getMessage(), e);
            }
        }

        // 2️⃣ Evaluate KYC rules (they create alerts separately)
        try {
            kycRuleEvaluator.evaluateKycRulesForTransaction(transaction);
            logger.info("KYC rule evaluation completed successfully");
        } catch (Exception e) {
            logger.error("Error during KYC rule evaluation: {}", e.getMessage(), e);
        }

        // 3️⃣ Cap raw risk score at 100
        int ruleScore = Math.min(100, rawRiskScore);

        // 4️⃣ Optional: Boost for very large amounts (e.g., >1M INR)
        BigDecimal amount = Optional.ofNullable(transaction.getAmount()).orElse(BigDecimal.ZERO);
        BigDecimal reference = new BigDecimal("1000000"); // 1M INR
        boolean isHighValue = amount.compareTo(reference) >= 0;

        int finalScore = ruleScore;
        if (isHighValue && ruleScore < 70) {
            // Ensure high-value transactions with any red flag get flagged
            finalScore = Math.max(finalScore, 70);
        }

        // 5️⃣ Determine action
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

        // 6️⃣ Log summary
        logger.info("=== EVALUATION SUMMARY ===");
        logger.info("Raw Risk Score: {} → Final Risk Score: {}/100", rawRiskScore, finalScore);
        logger.info("Triggered Rules: {}", triggeredRules);
        logger.info("Transaction Action: {}", action);
        logger.info("=== AML RULE EVALUATION END ===");

        return suspicious 
            ? RuleEngineResult.suspicious(finalScore, triggeredRules)
            : RuleEngineResult.clean(finalScore, triggeredRules);
    }
}