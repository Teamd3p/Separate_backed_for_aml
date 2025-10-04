package com.tss.aml.rule;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.SuspiciousKeyword;
import com.tss.aml.entity.Transaction;
import com.tss.aml.repository.SuspiciousKeywordRepository;

@Component
public class KeywordRuleEvaluator implements RuleEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(KeywordRuleEvaluator.class);

    @Autowired
    private SuspiciousKeywordRepository keywordRepository;

    @Override
    public boolean supports(String ruleType) {
        return "KEYWORD".equals(ruleType);
    }

    @Override
    public boolean evaluate(Transaction tx, Rule rule) {
        String desc = tx.getDescription();
        if (desc == null || desc.isEmpty()) {
            logger.debug("✅ KEYWORD PASSED: {} | No description to check", rule.getName());
            return false;
        }

        String lowerDesc = desc.toLowerCase();
        List<SuspiciousKeyword> keywords = keywordRepository.findByIsActiveTrue();
        
        logger.debug("🔍 KEYWORD Rule: {} | Checking description '{}' against {} keywords", 
            rule.getName(), desc, keywords.size());

        for (SuspiciousKeyword kw : keywords) {
            if (lowerDesc.contains(kw.getWord().toLowerCase())) {
                logger.warn("⚠️ KEYWORD TRIGGERED: {} | Found suspicious keyword '{}' in description '{}'", 
                    rule.getName(), kw.getWord(), desc);
                return true;
            }
        }

        logger.debug("✅ KEYWORD PASSED: {} | No suspicious keywords found in '{}'", 
            rule.getName(), desc);
        return false;
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        return rule.getRiskScoreImpact();
    }
}