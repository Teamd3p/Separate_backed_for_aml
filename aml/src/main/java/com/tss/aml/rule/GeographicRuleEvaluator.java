package com.tss.aml.rule;

import com.tss.aml.entity.*;
import com.tss.aml.repository.RiskyCountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeographicRuleEvaluator implements RuleEvaluator {

    @Autowired
    private RiskyCountryRepository riskyCountryRepository;

    @Override
    public boolean supports(String ruleType) {
        return "GEOGRAPHIC".equals(ruleType);
    }

    @Override
    public boolean evaluate(Transaction tx, Rule rule) {
        if (tx.getCountryCode() == null) return false;
        RiskyCountry country = riskyCountryRepository.findById(tx.getCountryCode()).orElse(null);
        if (country == null) return false;

        return country.getRiskLevel() == RiskLevel.HIGH ||
               country.getRiskLevel() == RiskLevel.CRITICAL;
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        return rule.getRiskScoreImpact();
    }
}