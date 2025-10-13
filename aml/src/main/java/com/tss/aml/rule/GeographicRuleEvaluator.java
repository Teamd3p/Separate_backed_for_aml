package com.tss.aml.rule;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import com.tss.aml.repository.RiskyCountryRepository;

@Component
public class GeographicRuleEvaluator implements RuleEvaluator {

	private static final Logger logger = LoggerFactory.getLogger(GeographicRuleEvaluator.class);

	@Autowired
	private RiskyCountryRepository riskyCountryRepository;

	@Override
	public boolean supports(String ruleType) {
		return "GEOGRAPHIC".equalsIgnoreCase(ruleType);
	}

	@Override
	public boolean evaluate(Transaction tx, Rule rule) {
		try {
			String country = Optional.ofNullable(tx).map(Transaction::getCountryCode).orElse(null);
			logger.info("🌍 GEOGRAPHIC RULE EVALUATION - Rule: {}", rule.getName());
			logger.info("🌍 Transaction country code: '{}'", country);
			
			if (country == null || country.trim().isEmpty()) {
				logger.warn("🌍 No country code found in transaction - geographic rule cannot evaluate");
				return false;
			}
			
			logger.info("🌍 Checking if country '{}' exists in risky countries database", country);
			return riskyCountryRepository.findById(country).map(rc -> {
				logger.warn("⚠️ GEOGRAPHIC RULE TRIGGERED: {} | country={} | riskLevel={}", 
						rule.getName(), country, rc.getRiskLevel());
				return true;
			}).orElseGet(() -> {
				logger.info("🌍 Country '{}' not found in risky countries database - rule not triggered", country);
				return false;
			});
		} catch (Exception e) {
			logger.error("❌ Error evaluating geographic rule {}: {}", rule.getName(), e.getMessage());
			return false;
		}
	}

	@Override
	public int getRiskScoreImpact(Rule rule) {
		return Optional.ofNullable(rule).map(Rule::getRiskScoreImpact).orElse(50);
	}
}