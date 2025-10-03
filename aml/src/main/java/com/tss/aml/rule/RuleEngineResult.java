package com.tss.aml.rule;

import java.util.List;

public class RuleEngineResult {
	private final boolean suspicious;
	private final int totalRiskScore;
	private final List<String> triggeredRuleNames;

	public RuleEngineResult(boolean suspicious, int totalRiskScore, List<String> triggeredRuleNames) {
		this.suspicious = suspicious;
		this.totalRiskScore = totalRiskScore;
		this.triggeredRuleNames = triggeredRuleNames;
	}

	public static RuleEngineResult clean() {
		return new RuleEngineResult(false, 0, java.util.Collections.emptyList());
	}

	public static RuleEngineResult suspicious(int score, List<String> rules) {
		return new RuleEngineResult(true, score, rules);
	}

	// Getters
	public boolean isSuspicious() {
		return suspicious;
	}

	public int getTotalRiskScore() {
		return totalRiskScore;
	}

	public List<String> getTriggeredRuleNames() {
		return triggeredRuleNames;
	}
}