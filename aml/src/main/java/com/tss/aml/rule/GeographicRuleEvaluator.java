package com.tss.aml.rule;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.enums.RiskLevel;
import com.tss.aml.repository.RiskyCountryRepository;
import com.tss.aml.util.ObjectMapperHolder;

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
            String countryCode = Optional.ofNullable(tx)
                .map(Transaction::getCountryCode)
                .filter(code -> !code.trim().isEmpty())
                .orElse(null);

            if (countryCode == null) {
                logger.debug("🌍 No country code in transaction – skipping geographic rule");
                return false;
            }

            return riskyCountryRepository.findById(countryCode)
                .map(riskyCountry -> {
                    RiskLevel riskLevel = riskyCountry.getRiskLevel();
                    BigDecimal amount = Optional.ofNullable(tx.getAmount()).orElse(BigDecimal.ZERO);

                    // Parse thresholds from rule conditions (with fallbacks)
                    Map<String, Object> conditions = null;
                    try {
                        conditions = ObjectMapperHolder.readMap(rule.getConditions());
                    } catch (Exception e) {
                        logger.warn("⚠️ Failed to parse GEOGRAPHIC rule conditions – using defaults");
                    }

                    BigDecimal highThreshold = getThreshold(conditions, "highRiskAmountThreshold", new BigDecimal("50000"));
                    BigDecimal mediumThreshold = getThreshold(conditions, "mediumRiskAmountThreshold", new BigDecimal("500000"));

                    boolean shouldTrigger = false;

                    switch (riskLevel) {
                        case CRITICAL:
                            // Block ALL transactions – no amount check
                            shouldTrigger = true;
                            logger.warn("🚨 CRITICAL RISK COUNTRY DETECTED: {} | Amount: {} | ACTION: BLOCK", 
                                countryCode, amount);
                            break;

                        case HIGH:
                            if (amount.compareTo(highThreshold) >= 0) {
                                shouldTrigger = true;
                                logger.warn("⚠️ HIGH RISK COUNTRY + LARGE AMOUNT: {} | {} ≥ {} | ACTION: FLAG", 
                                    countryCode, amount, highThreshold);
                            } else {
                                logger.debug("✅ HIGH RISK COUNTRY BELOW THRESHOLD: {} | {} < {}", 
                                    countryCode, amount, highThreshold);
                            }
                            break;

                        case MEDIUM:
                            if (amount.compareTo(mediumThreshold) >= 0) {
                                shouldTrigger = true;
                                logger.warn("⚠️ MEDIUM RISK COUNTRY + VERY LARGE AMOUNT: {} | {} ≥ {} | ACTION: FLAG", 
                                    countryCode, amount, mediumThreshold);
                            } else {
                                logger.debug("✅ MEDIUM RISK COUNTRY BELOW THRESHOLD: {} | {} < {}", 
                                    countryCode, amount, mediumThreshold);
                            }
                            break;

                        default:
                            // LOW or unknown – do not trigger
                            logger.debug("✅ Country {} is {} risk – ignored", countryCode, riskLevel);
                            shouldTrigger = false;
                    }

                    return shouldTrigger;
                })
                .orElse(false);

        } catch (Exception e) {
            logger.error("❌ Error evaluating geographic rule {}: {}", 
                Optional.ofNullable(rule).map(Rule::getName).orElse("UNKNOWN"), e.getMessage(), e);
            return false;
        }
    }

    private BigDecimal getThreshold(Map<String, Object> conditions, String key, BigDecimal defaultValue) {
        if (conditions != null) {
            try {
                Object value = conditions.get(key);
                if (value instanceof Number) {
                    return new BigDecimal(value.toString());
                } else if (value instanceof String) {
                    return new BigDecimal((String) value);
                }
            } catch (Exception ex) {
                logger.warn("⚠️ Invalid threshold for '{}': {} – using default {}", key, conditions.get(key), defaultValue);
            }
        }
        return defaultValue;
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        // This is a fallback; actual impact should be scaled in RuleEngineServiceImpl if needed
        return Optional.ofNullable(rule).map(Rule::getRiskScoreImpact).orElse(50);
    }
}