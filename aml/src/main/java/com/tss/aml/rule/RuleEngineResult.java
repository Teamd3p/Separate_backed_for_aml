package com.tss.aml.rule;
import java.util.List;

public class RuleEngineResult {
    private final boolean suspicious;
    private final int riskScore;
    private final List<String> triggeredRules;

    private RuleEngineResult(boolean suspicious, int riskScore, List<String> triggeredRules) {
        this.suspicious = suspicious;
        this.riskScore = riskScore;
        this.triggeredRules = triggeredRules;
    }

    public static RuleEngineResult suspicious(int riskScore, List<String> triggeredRules) {
        return new RuleEngineResult(true, riskScore, triggeredRules);
    }

    public static RuleEngineResult clean(int riskScore, List<String> triggeredRules) {
        return new RuleEngineResult(false, riskScore, triggeredRules);
    }

    public boolean isSuspicious() { return suspicious; }
    public int getRiskScore() { return riskScore; }
    public List<String> getTriggeredRules() { return triggeredRules; }
}
