package com.tss.aml.rule;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.SuspiciousKeyword;
import com.tss.aml.entity.Transaction;
import com.tss.aml.repository.SuspiciousKeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KeywordRuleEvaluator implements RuleEvaluator {

    @Autowired
    private SuspiciousKeywordRepository keywordRepository;

    @Override
    public boolean supports(String ruleType) {
        return "KEYWORD".equals(ruleType);
    }

    @Override
    public boolean evaluate(Transaction tx, Rule rule) {
        String desc = tx.getDescription();
        if (desc == null || desc.isEmpty()) return false;

        String lowerDesc = desc.toLowerCase();
        List<SuspiciousKeyword> keywords = keywordRepository.findByIsActiveTrue();

        return keywords.stream()
            .anyMatch(kw -> lowerDesc.contains(kw.getWord().toLowerCase()));
    }

    @Override
    public int getRiskScoreImpact(Rule rule) {
        return rule.getRiskScoreImpact();
    }
}