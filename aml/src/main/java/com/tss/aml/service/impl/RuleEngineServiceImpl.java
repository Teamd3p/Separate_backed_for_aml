package com.tss.aml.service.impl;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private List<RuleEvaluator> evaluators;

    @Autowired
    private RuleRepository ruleRepository;

    @Override
    public RuleEngineResult evaluate(Transaction transaction) {
        List<Rule> activeRules = ruleRepository.findByIsActiveTrue();
        List<String> triggered = new ArrayList<>();
        int totalRisk = 0;

        for (Rule rule : activeRules) {
            RuleEvaluator evaluator = evaluators.stream()
                .filter(e -> e.supports(rule.getType().name()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No evaluator for rule type: " + rule.getType()));

            if (evaluator.evaluate(transaction, rule)) {
                triggered.add(rule.getName());
                totalRisk += evaluator.getRiskScoreImpact(rule);
            }
        }

        return triggered.isEmpty() ?
            RuleEngineResult.clean() :
            RuleEngineResult.suspicious(totalRisk, triggered);
    }
}