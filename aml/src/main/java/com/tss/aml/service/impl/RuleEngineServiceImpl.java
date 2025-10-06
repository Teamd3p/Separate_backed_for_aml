package com.tss.aml.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import com.tss.aml.repository.RuleRepository;
import com.tss.aml.rule.RuleEngineResult;
import com.tss.aml.rule.RuleEvaluator;
import com.tss.aml.service.RuleEngineService;

@Service
public class RuleEngineServiceImpl implements RuleEngineService {

	private static final Logger logger = LoggerFactory.getLogger(RuleEngineServiceImpl.class);

	@Autowired
	private List<RuleEvaluator> evaluators;

	@Autowired
	private RuleRepository ruleRepository;

	@Override
	public RuleEngineResult evaluate(Transaction transaction) {
		logger.info("=== AML RULE EVALUATION START ===");
		logger.info("Transaction ID: {}, Amount: {} {}, Type: {}, Customer: {}", transaction.getTransactionId(),
				transaction.getAmount(), transaction.getCurrency(), transaction.getTransactionType(),
				transaction.getCustomer() != null ? transaction.getCustomer().getUserId() : "Unknown");

		List<Rule> activeRules = ruleRepository.findByIsActiveTrue();
		logger.info("Found {} active rules to evaluate", activeRules.size());

		List<String> triggered = new ArrayList<>();
		int totalRisk = 0;
		int maxPossibleRisk = 0;

		// Calculate total possible risk for normalization
		for (Rule rule : activeRules) {
			maxPossibleRisk += rule.getRiskScoreImpact();
		}
		logger.info("Maximum possible risk score: {}", maxPossibleRisk);

		for (Rule rule : activeRules) {
			try {
				logger.debug("Evaluating rule: {} (Type: {}, Risk Impact: {})", rule.getName(), rule.getType(),
						rule.getRiskScoreImpact());

				RuleEvaluator evaluator = evaluators.stream().filter(e -> e.supports(rule.getType().name())).findFirst()
						.orElse(null);

				if (evaluator == null) {
					logger.warn("No evaluator found for rule type: {} (Rule: {})", rule.getType(), rule.getName());
					continue;
				}

				boolean ruleTriggered = evaluator.evaluate(transaction, rule);
				if (ruleTriggered) {
					triggered.add(rule.getName());
					totalRisk += evaluator.getRiskScoreImpact(rule);
					logger.warn("🚨 RULE TRIGGERED: {} | Risk Impact: {} | Total Risk: {}", rule.getName(),
							evaluator.getRiskScoreImpact(rule), totalRisk);
				} else {
					logger.debug("✅ Rule passed: {}", rule.getName());
				}
			} catch (Exception e) {
				logger.error("❌ Error evaluating rule {}: {}", rule.getName(), e.getMessage(), e);
			}
		}

		// Normalize risk score to 0-100 range
//        int finalRiskScore = maxPossibleRisk > 0 ? 
//            Math.min(100, (totalRisk * 100) / maxPossibleRisk) : 0;

		int finalRiskScore = Math.min(100, totalRisk);

		logger.info("=== EVALUATION SUMMARY ===");
		logger.info("Raw Risk Score: {} / {}", totalRisk, maxPossibleRisk);
		logger.info("Normalized Risk Score: {}/100", finalRiskScore);
		logger.info("Triggered Rules: {}", triggered);
		logger.info("Transaction Status: {}", triggered.isEmpty() ? "CLEAN"
				: (finalRiskScore >= 85 ? "BLOCKED" : (finalRiskScore >= 50 ? "FLAGGED" : "COMPLETED")));
		logger.info("=== AML RULE EVALUATION END ===");

		return triggered.isEmpty() ? RuleEngineResult.clean() : RuleEngineResult.suspicious(finalRiskScore, triggered);
	}
}