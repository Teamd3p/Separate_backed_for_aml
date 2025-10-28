package com.tss.aml.rule;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.Transaction;

public interface RuleEvaluator {
    boolean supports(String ruleType);
    boolean evaluate(Transaction transaction, Rule rule);
    int getRiskScoreImpact(Rule rule);
}