package com.tss.aml.rule;

import java.util.ArrayList;
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
			logger.debug("✅ KEYWORD PASSED: {} | No description", rule.getName());
			return false;
		}

		String lowerDesc = " " + desc.toLowerCase() + " "; // For word boundaries
		List<SuspiciousKeyword> keywords = keywordRepository.findByIsActiveTrue();
		int matchedCount = 0;
		int maxSeverity = 0;
		List<String> foundKeywords = new ArrayList<>();

		for (SuspiciousKeyword kw : keywords) {
			String word = kw.getWord().toLowerCase();
			// Handle multi-word keywords (e.g., "crypto mixer")
			if (lowerDesc.contains(word)) {
				matchedCount++;
				maxSeverity = Math.max(maxSeverity, kw.getSeverity());
				foundKeywords.add(kw.getWord());

				// Optional: Stop early if severity is critical
				if (kw.getSeverity() >= 9)
					break;
			}
		}

		if (matchedCount > 0) {
			logger.warn("⚠️ KEYWORD TRIGGERED: {} | Found {} keywords: {} (Max Severity: {})", rule.getName(),
					matchedCount, foundKeywords, maxSeverity);

			return true;
		}

		logger.debug("✅ KEYWORD PASSED: {} | No matches in '{}'", rule.getName(), desc);
		return false;
	}

	public int calculateKeywordRisk(Transaction transaction, Rule rule) {
		String desc = transaction.getDescription();
		if (desc == null || desc.isEmpty()) {
			return 0;
		}

		String lowerDesc = " " + desc.toLowerCase() + " "; // Word boundary padding
		List<SuspiciousKeyword> activeKeywords = keywordRepository.findByIsActiveTrue();

		int maxSeverity = 0;
		int matchCount = 0;
		boolean hasCriticalKeyword = false;

		for (SuspiciousKeyword kw : activeKeywords) {
			String word = kw.getWord().toLowerCase();
			// Handle multi-word phrases (e.g., "crypto mixer")
			if (lowerDesc.contains(word)) {
				matchCount++;
				maxSeverity = Math.max(maxSeverity, kw.getSeverity());
				if (kw.getSeverity() >= 9) {
					hasCriticalKeyword = true;
				}
			}
		}

		if (matchCount == 0) {
			return 0;
		}

		// Base score from max severity (scale 1-10 → 50-95)
		int baseScore = 50 + (maxSeverity * 4); // e.g., severity 10 → 90

		// Boost for multiple matches
		if (matchCount >= 3) {
			baseScore += 10;
		} else if (matchCount == 2) {
			baseScore += 5;
		}

		// Critical keyword override
		if (hasCriticalKeyword) {
			baseScore = Math.max(baseScore, 90);
		}

		// Cap at 100
		return Math.min(100, baseScore);
	}

	@Override
	public int getRiskScoreImpact(Rule rule) {
		return rule.getRiskScoreImpact();
	}
}