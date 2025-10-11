package com.tss.aml.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tss.aml.entity.Rule;
import com.tss.aml.entity.enums.RuleType;

@Configuration
public class RuleInitializer {

    @Bean
    public List<Rule> initializeRules() {
        return Arrays.asList(
            // ---------- Geographic Rules ----------
            new Rule("Critical Risk Country Block",
                    RuleType.GEOGRAPHIC,
                    "{\"riskType\": \"CRITICAL\", \"listName\": \"critical_country_list\"}",
                    95),

            new Rule("High-Risk FX Corridor Transfer",
                    RuleType.GEOGRAPHIC,
                    "{\"riskCorridorsListName\": \"fx_corridor_list\", \"mediumRiskHistory\": {\"countryRiskLevel\": \"MEDIUM\", \"lookbackDays\": 90, \"transactionCount\": 2}}",
                    85),

            new Rule("Sanctions List Match",
                    RuleType.GEOGRAPHIC,
                    "{\"listName\": \"sanctions_list\"}",
                    90),

            // ---------- Threshold Rules ----------
            new Rule("High-Value Transfer - Current",
                    RuleType.THRESHOLD,
                    "{\"accountType\": \"CURRENT\", \"amountThreshold\": 1000000, \"dailyLimitCount\": 1}",
                    70),

            new Rule("High-Value Transfer - Saving",
                    RuleType.THRESHOLD,
                    "{\"accountType\": \"SAVING\", \"amountThreshold\": 100000, \"transactionCountThreshold\": 3}",
                    65),

            new Rule("High Value Transaction - Critical",
                    RuleType.THRESHOLD,
                    "{\"threshold\": 500000, \"currency\": \"INR\", \"operator\": \"GREATER_THAN\"}",
                    80),

            new Rule("Multiple Large Transfers in 24h",
                    RuleType.THRESHOLD,
                    "{\"timeWindowHours\": 24, \"amountThreshold\": 200000, \"maxCount\": 3}",
                    75),

            // ---------- Pattern Detection Rules ----------
            new Rule("Structuring/Smurfing Detection",
                    RuleType.PATTERN,
                    "{\"structuringPattern\": {\"timeWindowMinutes\": 1440, \"transactionType\": \"CREDIT\"}, \"microburstPattern\": {\"timeWindowMinutes\": 5, \"minTransactions\": 3, \"transactionType\": \"DEBIT\"}}",
                    90),

            new Rule("Salary Account Pass-Through",
                    RuleType.PATTERN,
                    "{\"accountType\": \"SALARY\", \"payoutPercentage\": 90, \"timeWindowHours\": 48}",
                    85),

            new Rule("Round Amount Structuring",
                    RuleType.PATTERN,
                    "{\"threshold\": 5, \"timeWindow\": \"1_DAY\", \"roundTo\": 1000}",
                    55),

            new Rule("Keyword Suspicious Transaction",
                    RuleType.PATTERN,
                    "{\"regex\": \"lottery|prize|gift|urgent|inheritance\"}",
                    80),

            // ---------- Velocity Rules ----------
            new Rule("Rapid Deposit-Transfer Pattern",
                    RuleType.VELOCITY,
                    "{\"timeWindowMinutes\": 60, \"minAmount\": 5000, \"amountMatchPercentage\": 90}",
                    80),

            new Rule("Rapid Currency Cycling",
                    RuleType.VELOCITY,
                    "{\"minDistinctCurrencies\": 3, \"timeWindowMinutes\": 60}",
                    75),

            new Rule("High Transfer Velocity",
                    RuleType.VELOCITY,
                    "{\"timeWindowMinutes\": 120, \"minAmountUSD\": 2000, \"maxTransactions\": 5}",
                    80),

            // ---------- Frequency Rules ----------
            new Rule("Rapid Withdrawals",
                    RuleType.FREQUENCY,
                    "{\"transactionTypes\": [\"DEBIT\",\"ATM_WITHDRAWAL\"], \"referenceRule\": \"High-Value Transfer - Current\"}",
                    75),

            new Rule("Multiple International Transfers",
                    RuleType.FREQUENCY,
                    "{\"transactionTypes\": [\"WIRE_TRANSFER\"], \"maxCount\": 3, \"timeWindowHours\": 24}",
                    70),

            // ---------- Behavioral Rules ----------
            new Rule("Dormant Account Activation",
                    RuleType.BEHAVIOR,
                    "{\"dormantDays\": 180, \"minAmountUSD\": 1, \"checkFlaggedHistory\": true}",
                    90),

            new Rule("Multi-Currency Profile Activity",
                    RuleType.BEHAVIOR,
                    "{\"profileTrigger\": {\"minCurrencyAccounts\": 3}, \"specialThresholds\": {\"internationalTransferLimitUSD\": 5000}}",
                    65),

            new Rule("Unusual Business Hours Activity",
                    RuleType.BEHAVIOR,
                    "{\"accountType\": \"CURRENT\", \"timeWindow\": \"22:00-06:00\", \"minAmountUSD\": 5000}",
                    70),

            new Rule("High-Fee FX Conversion Frequency",
                    RuleType.BEHAVIOR,
                    "{\"feePercentageThreshold\": 2.5, \"minTransactions\": 3, \"timeWindowHours\": 72}",
                    70),

            // ---------- Graph Rules ----------
            new Rule("Fan-In / Fan-Out Pattern",
                    RuleType.GRAPH_PATTERN,
                    "{\"pattern\": [\"FAN_IN\", \"FAN_OUT\"], \"timeWindowHours\": 24, \"minEdgeCount\": 5}",
                    90),

            new Rule("Newly Connected High-Risk Entity",
                    RuleType.GRAPH_BEHAVIOR,
                    "{\"trigger\": \"NEW_CONNECTION\", \"targetRiskScore\": 90, \"targetLists\": [\"internal_blacklist\", \"sanctions_list\"]}",
                    95)
        );
    }
}
