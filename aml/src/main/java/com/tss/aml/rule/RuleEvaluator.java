package com.tss.aml.rule;

import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.Rule;

public interface RuleEvaluator {
    boolean supports(String ruleType);
    boolean evaluate(Transaction transaction, Rule rule);
    int getRiskScoreImpact(Rule rule);
}