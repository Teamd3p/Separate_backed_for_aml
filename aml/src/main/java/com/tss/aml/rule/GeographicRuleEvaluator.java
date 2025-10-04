package com.tss.aml.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tss.aml.entity.RiskyCountry;
import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.enums.RiskLevel;
import com.tss.aml.repository.RiskyCountryRepository;

@Component
public class GeographicRuleEvaluator implements RuleEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(GeographicRuleEvaluator.class);

    @Autowired
    private RiskyCountryRepository riskyCountryRepository;

    @Override
    public boolean supports(String ruleType) {
        return "GEOGRAPHIC".equals(ruleType);
    }

    @Override
    public boolean evaluate(Transaction tx, Rule rule) {
        if (tx.getCountryCode() == null) {
            logger.debug("✅ GEOGRAPHIC PASSED: {} | No country code to check", rule.getName());
            return false;
        }
        
        RiskyCountry country = riskyCountryRepository.findById(tx.getCountryCode()).orElse(null);
        if (country == null) {
            logger.debug("✅ GEOGRAPHIC PASSED: {} | Country '{}' not in risky countries list", 
                rule.getName(), tx.getCountryCode());
            return false;
        }

        logger.debug("🔍 GEOGRAPHIC Rule: {} | Checking country '{}' with risk level '{}'", 
            rule.getName(), tx.getCountryCode(), country.getRiskLevel());

        boolean triggered = country.getRiskLevel() == RiskLevel.HIGH ||
                           country.getRiskLevel() == RiskLevel.CRITICAL;

        if (triggered) {
            logger.warn("⚠️ GEOGRAPHIC TRIGGERED: {} | Country '{}' has {} risk level", 
                rule.getName(), tx.getCountryCode(), country.getRiskLevel());
        } else {
            logger.debug("✅ GEOGRAPHIC PASSED: {} | Country '{}' has {} risk level", 
                rule.getName(), tx.getCountryCode(), country.getRiskLevel());
        }

        return triggered;
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        return rule.getRiskScoreImpact();
    }
}