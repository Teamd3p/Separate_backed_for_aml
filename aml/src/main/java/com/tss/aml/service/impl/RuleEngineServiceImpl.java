package com.tss.aml.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.aml.entity.Alert;
import com.tss.aml.entity.Rule;
import com.tss.aml.entity.SuspiciousKeyword;
import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.enums.AlertStatus;
import com.tss.aml.repository.AlertRepository;
import com.tss.aml.repository.RuleRepository;
import com.tss.aml.repository.SuspiciousKeywordRepository;
import com.tss.aml.rule.KeywordRuleEvaluator;
import com.tss.aml.rule.KycRuleEvaluator;
import com.tss.aml.rule.RuleEngineResult;
import com.tss.aml.rule.RuleEvaluator;
import com.tss.aml.service.RuleEngineService;

@Service
@Transactional
public class RuleEngineServiceImpl implements RuleEngineService {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineServiceImpl.class);

    // Thresholds for normalized risk score
    private static final int BLOCK_THRESHOLD = 90;
    private static final int FLAG_THRESHOLD = 60;

    private final RuleRepository ruleRepository;
    private final SuspiciousKeywordRepository suspiciousKeywordRepository;
    private final List<RuleEvaluator> evaluators;
    private final KycRuleEvaluator kycRuleEvaluator;
    private final AlertRepository alertRepository;

    @Autowired
    public RuleEngineServiceImpl(RuleRepository ruleRepository,
            SuspiciousKeywordRepository suspiciousKeywordRepository, List<RuleEvaluator> evaluators,
            KycRuleEvaluator kycRuleEvaluator, AlertRepository alertRepository) {
        this.ruleRepository = ruleRepository;
        this.suspiciousKeywordRepository = suspiciousKeywordRepository;
        this.evaluators = evaluators;
        this.kycRuleEvaluator = kycRuleEvaluator;
        this.alertRepository = alertRepository;
    }

    @Override
    public RuleEngineResult evaluate(Transaction transaction) {
        logger.info("=== AML RULE EVALUATION START ===");
        logger.info("Transaction ID: {}, Amount: {} {}, Type: {}, Customer: {}", transaction.getTransactionId(),
                transaction.getAmount(), transaction.getCurrency(), transaction.getTransactionType(),
                transaction.getCustomer() != null ? transaction.getCustomer().getUserId() : "Unknown");

        List<Rule> activeRules = ruleRepository.findByIsActiveTrue();
        List<SuspiciousKeyword> activeKeywords = suspiciousKeywordRepository.findByIsActiveTrue();

        logger.info("Found {} active rules and {} active keywords", activeRules.size(), activeKeywords.size());

        List<String> triggeredRules = new ArrayList<>();
        int totalScore = 0;

        int maxRuleScore = 0;
        int maxKeywordScore = 0;

        // 1️⃣ Evaluate DB-driven AML rules
        for (Rule rule : activeRules) {
            try {
                maxRuleScore += rule.getRiskScoreImpact(); // Sum max possible score

                RuleEvaluator evaluator = evaluators.stream()
                        .filter(ev -> ev.supports(rule.getType().name()))
                        .findFirst()
                        .orElse(null);

                if (evaluator == null) {
                    logger.warn("No evaluator found for rule type: {} (Rule: {})", rule.getType(), rule.getName());
                    continue;
                }

                if (evaluator.evaluate(transaction, rule)) {
                    triggeredRules.add(rule.getName());
                    int riskImpact = (evaluator instanceof KeywordRuleEvaluator) 
                            ? ((KeywordRuleEvaluator) evaluator).calculateKeywordRisk(transaction, rule)
                            : evaluator.getRiskScoreImpact(rule);

                    totalScore += riskImpact;
                    logger.warn("🚨 RULE TRIGGERED: {} | Risk Impact: {} | Running Total: {}", rule.getName(), riskImpact, totalScore);
                }
            } catch (Exception e) {
                logger.error("❌ Error evaluating rule {}: {}", rule.getName(), e.getMessage(), e);
            }
        }

        // 2️⃣ Evaluate Keyword-based risk
        Map<String, Integer> keywordHits = detectKeywords(transaction.getDescription(), activeKeywords);
        int keywordScore = keywordHits.values().stream().mapToInt(Integer::intValue).sum();
        maxKeywordScore = activeKeywords.stream().mapToInt(kw -> Math.max(10, kw.getSeverity() * 10)).sum();
        totalScore += keywordScore;

        keywordHits.forEach((kw, score) -> {
            triggeredRules.add("Keyword: " + kw);
            logger.warn("🔍 KEYWORD DETECTED: {} | Score: {}", kw, score);
        });

        // 3️⃣ Evaluate KYC-specific rules
        logger.info("=== KYC RULE EVALUATION ===");
        try {
            kycRuleEvaluator.evaluateKycRulesForTransaction(transaction);
            logger.info("KYC rule evaluation completed successfully");
        } catch (Exception e) {
            logger.error("Error during KYC rule evaluation: {}", e.getMessage(), e);
        }

        // 4️⃣ Normalize total score to 0–100
        int combinedMaxScore = maxRuleScore + maxKeywordScore;
        combinedMaxScore = combinedMaxScore == 0 ? 1 : combinedMaxScore; // avoid division by zero

        
        int normalizedScore = Math.min(totalScore, 100);
        // 5️⃣ Determine action based on thresholds
        boolean suspicious = normalizedScore > FLAG_THRESHOLD;
        String action;
        if (normalizedScore > BLOCK_THRESHOLD) action = "BLOCK";
        else if (normalizedScore > FLAG_THRESHOLD) action = "FLAG";
        else action = "ALLOW";

   
        logger.info("=== EVALUATION SUMMARY ===");
        logger.info("Final Normalized Risk Score: {}/100", normalizedScore);
        logger.info("Triggered Rules: {}", triggeredRules);
        logger.info("Transaction Action: {}", action);
        logger.info("=== AML RULE EVALUATION END ===");

       
        return suspicious ? 
            RuleEngineResult.suspicious(normalizedScore, triggeredRules) : 
            RuleEngineResult.clean(normalizedScore, triggeredRules);
    }

    private Map<String, Integer> detectKeywords(String description, List<SuspiciousKeyword> activeKeywords) {
        if (description == null || description.isEmpty()) return Collections.emptyMap();

        String lowerDesc = description.toLowerCase();
        Map<String, Integer> matchedKeywords = new LinkedHashMap<>();

        for (SuspiciousKeyword kw : activeKeywords) {
            if (lowerDesc.contains(kw.getWord().toLowerCase())) {
                int riskScore = Math.max(10, kw.getSeverity() * 10);
                matchedKeywords.put(kw.getWord(), riskScore);
            }
        }
        return matchedKeywords;
    }

}
