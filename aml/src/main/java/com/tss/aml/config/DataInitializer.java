package com.tss.aml.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tss.aml.entity.Admin;
import com.tss.aml.entity.ComplianceOfficer;
import com.tss.aml.entity.RiskyCountry;
import com.tss.aml.entity.Rule;
import com.tss.aml.entity.SuspiciousKeyword;
import com.tss.aml.entity.enums.RiskLevel;
import com.tss.aml.entity.enums.Role;
import com.tss.aml.entity.enums.RuleType;
import com.tss.aml.entity.enums.UserStatus;
import com.tss.aml.repository.ComplianceOfficerRepository;
import com.tss.aml.repository.RiskyCountryRepository;
import com.tss.aml.repository.RuleRepository;
import com.tss.aml.repository.SuspiciousKeywordRepository;
import com.tss.aml.repository.UserRepository;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            ComplianceOfficerRepository officerRepository,
            RuleRepository ruleRepository,
            SuspiciousKeywordRepository keywordRepository,
            RiskyCountryRepository countryRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            logger.info("Starting data initialization...");

            // Initialize Admin User
            initializeAdmin(userRepository, passwordEncoder);

            // Initialize Compliance Officers
//            initializeComplianceOfficers(officerRepository, passwordEncoder);

            // Initialize AML Rules
            initializeAMLRules(ruleRepository);

            // Initialize Suspicious Keywords
            initializeSuspiciousKeywords(keywordRepository);

            // Initialize Risky Countries
            initializeRiskyCountries(countryRepository);

            logger.info("Data initialization completed successfully!");
        };
    }

    private void initializeAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        if (userRepository.findByEmail("admin@aml.com").isEmpty()) {
            Admin admin = new Admin();
            admin.setEmail("admin@aml.com");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin.setRole(Role.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setEmailVerified(true);
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setPhone("9999999999");

            userRepository.save(admin);
            logger.info("✓ Admin user created: admin@aml.com");
        } else {
            logger.info("✓ Admin user already exists");
        }
    }

    private void initializeComplianceOfficers(ComplianceOfficerRepository officerRepository, PasswordEncoder passwordEncoder) {
        String[][] officers = {
                {"sarah.williams@aml.com", "Sarah", "Williams", "9876543211"},
                {"john.smith@aml.com", "John", "Smith", "9876543212"},
                {"emily.chen@aml.com", "Emily", "Chen", "9876543213"}
        };

        int created = 0;
        for (String[] officerData : officers) {
            if (officerRepository.findByEmail(officerData[0]).isEmpty()) {
                ComplianceOfficer officer = new ComplianceOfficer();
                officer.setEmail(officerData[0]);
                officer.setPasswordHash(passwordEncoder.encode("Officer@123"));
                officer.setRole(Role.COMPLIANCE_OFFICER);
                officer.setStatus(UserStatus.ACTIVE);
                officer.setEmailVerified(true);
                officer.setFirstName(officerData[1]);
                officer.setLastName(officerData[2]);
                officer.setPhone(officerData[3]);

                officerRepository.save(officer);
                created++;
            }
        }
        logger.info("✓ Compliance Officers: {} created, {} already exist", created, officers.length - created);
    }

    private void initializeAMLRules(RuleRepository ruleRepository) {
        if (ruleRepository.count() > 0) {
            logger.info("✓ AML Rules already exist ({} rules)", ruleRepository.count());
            return;
        }

        Rule[] rules = {
                createRule("High Value Transaction - Critical",
                        "Flag transactions above 500,000 INR as critical risk",
                        RuleType.THRESHOLD,
                        "{\"threshold\": 500000, \"currency\": \"INR\", \"operator\": \"GREATER_THAN\"}",
                        80),

                createRule("High Value Transaction - High",
                        "Flag transactions above 200,000 INR as high risk",
                        RuleType.THRESHOLD,
                        "{\"threshold\": 200000, \"currency\": \"INR\", \"operator\": \"GREATER_THAN\"}",
                        60),

                createRule("High Value Transaction - Medium",
                        "Flag transactions above 100,000 INR as medium risk",
                        RuleType.THRESHOLD,
                        "{\"threshold\": 100000, \"currency\": \"INR\", \"operator\": \"GREATER_THAN\"}",
                        40),

                createRule("Rapid Transaction Pattern",
                        "Flag accounts with more than 10 transactions in 1 hour",
                        RuleType.FREQUENCY,
                        "{\"count\": 10, \"timeWindow\": \"1_HOUR\", \"operator\": \"GREATER_THAN\"}",
                        70),

                createRule("Daily Transaction Limit Exceeded",
                        "Flag accounts with more than 20 transactions per day",
                        RuleType.FREQUENCY,
                        "{\"count\": 20, \"timeWindow\": \"1_DAY\", \"operator\": \"GREATER_THAN\"}",
                        50),

                createRule("Unusual Transaction Velocity",
                        "Flag sudden increase in transaction volume (3x average)",
                        RuleType.PATTERN,
                        "{\"multiplier\": 3, \"baseline\": \"AVERAGE\", \"period\": \"30_DAYS\"}",
                        65),

                createRule("Round Amount Structuring",
                        "Flag multiple round amount transactions (possible structuring)",
                        RuleType.PATTERN,
                        "{\"threshold\": 5, \"timeWindow\": \"1_DAY\", \"roundTo\": 1000}",
                        55),

                createRule("High Risk Country Transaction",
                        "Flag transactions involving high-risk countries",
                        RuleType.GEOGRAPHIC,
                        "{\"checkRiskyCountries\": true, \"minAmount\": 50000}",
                        75),

                createRule("Smurfing Pattern Detection",
                        "Detect multiple small transactions just below reporting threshold",
                        RuleType.PATTERN,
                        "{\"threshold\": 49000, \"count\": 5, \"timeWindow\": \"1_DAY\", \"tolerance\": 0.1}",
                        85),

                createRule("Dormant Account Reactivation",
                        "Flag large transactions from previously dormant accounts",
                        RuleType.PATTERN,
                        "{\"dormantPeriod\": \"180_DAYS\", \"minAmount\": 100000}",
                        60)
        };

        for (Rule rule : rules) {
            ruleRepository.save(rule);
        }
        logger.info("✓ {} AML Rules created", rules.length);
    }

    private Rule createRule(String name, String description, RuleType type, String conditions, int riskScore) {
        Rule rule = new Rule();
        rule.setName(name);
        rule.setDescription(description);
        rule.setType(type);
        rule.setConditions(conditions);
        rule.setRiskScoreImpact(riskScore);
        rule.setActive(true);
        return rule;
    }

    private void initializeSuspiciousKeywords(SuspiciousKeywordRepository keywordRepository) {
        if (keywordRepository.count() > 0) {
            logger.info("✓ Suspicious Keywords already exist ({} keywords)", keywordRepository.count());
            return;
        }

        String[][] keywords = {
                // Financial Crime
                {"laundering", "FINANCIAL_CRIME", "10"},
                {"money laundering", "FINANCIAL_CRIME", "10"},
                {"wash money", "FINANCIAL_CRIME", "9"},
                {"clean money", "FINANCIAL_CRIME", "9"},
                {"shell company", "FINANCIAL_CRIME", "8"},
                {"offshore", "FINANCIAL_CRIME", "7"},
                {"hawala", "FINANCIAL_CRIME", "9"},
                {"smurfing", "FINANCIAL_CRIME", "9"},
                {"structuring", "FINANCIAL_CRIME", "8"},

                // Terrorism Financing
                {"terrorism", "TERRORISM_FINANCING", "10"},
                {"terrorist", "TERRORISM_FINANCING", "10"},
                {"jihad", "TERRORISM_FINANCING", "9"},
                {"isis", "TERRORISM_FINANCING", "10"},
                {"al qaeda", "TERRORISM_FINANCING", "10"},
                {"funding terror", "TERRORISM_FINANCING", "10"},

                // Fraud
                {"fake invoice", "FRAUD", "8"},
                {"false documentation", "FRAUD", "8"},
                {"ponzi", "FRAUD", "9"},
                {"pyramid scheme", "FRAUD", "9"},
                {"scam", "FRAUD", "7"},
                {"fraudulent", "FRAUD", "8"},

                // Drug Trafficking
                {"narcotics", "DRUG_TRAFFICKING", "9"},
                {"drug money", "DRUG_TRAFFICKING", "10"},
                {"cocaine", "DRUG_TRAFFICKING", "9"},
                {"heroin", "DRUG_TRAFFICKING", "9"},
                {"cartel", "DRUG_TRAFFICKING", "9"},

                // Tax Evasion
                {"tax evasion", "TAX_EVASION", "8"},
                {"evade tax", "TAX_EVASION", "8"},
                {"undeclared income", "TAX_EVASION", "7"},
                {"black money", "TAX_EVASION", "9"},
                {"unreported", "TAX_EVASION", "6"},

                // Corruption
                {"bribe", "CORRUPTION", "9"},
                {"kickback", "CORRUPTION", "8"},
                {"corruption", "CORRUPTION", "8"},
                {"embezzlement", "CORRUPTION", "9"},
                {"political donation", "CORRUPTION", "6"},

                // Human Trafficking
                {"human trafficking", "HUMAN_TRAFFICKING", "10"},
                {"smuggling people", "HUMAN_TRAFFICKING", "9"},
                {"forced labor", "HUMAN_TRAFFICKING", "9"},

                // Weapons
                {"arms deal", "WEAPONS", "9"},
                {"weapon sale", "WEAPONS", "9"},
                {"illegal arms", "WEAPONS", "10"},

                // Suspicious Behavior
                {"cash only", "SUSPICIOUS_BEHAVIOR", "5"},
                {"no questions", "SUSPICIOUS_BEHAVIOR", "6"},
                {"urgent transfer", "SUSPICIOUS_BEHAVIOR", "5"},
                {"anonymous", "SUSPICIOUS_BEHAVIOR", "7"},
                {"untraceable", "SUSPICIOUS_BEHAVIOR", "8"}
        };

        for (String[] keywordData : keywords) {
            SuspiciousKeyword keyword = new SuspiciousKeyword();
            keyword.setWord(keywordData[0]);
            keyword.setCategory(keywordData[1]);
            keyword.setSeverity(Integer.parseInt(keywordData[2]));
            keyword.setActive(true);
            keywordRepository.save(keyword);
        }
        logger.info("✓ {} Suspicious Keywords created", keywords.length);
    }

    private void initializeRiskyCountries(RiskyCountryRepository countryRepository) {
        if (countryRepository.count() > 0) {
            logger.info("✓ Risky Countries already exist ({} countries)", countryRepository.count());
            return;
        }

        String[][] countries = {
                // FATF Black List
                {"KP", "North Korea", "CRITICAL"},
                {"IR", "Iran", "CRITICAL"},
                {"MM", "Myanmar", "HIGH"},

                // FATF Grey List
                {"SY", "Syria", "HIGH"},
                {"YE", "Yemen", "HIGH"},
                {"PK", "Pakistan", "MEDIUM"},
                {"JM", "Jamaica", "MEDIUM"},
                {"UG", "Uganda", "MEDIUM"},
                {"PH", "Philippines", "MEDIUM"},

                // Tax Havens
                {"KY", "Cayman Islands", "HIGH"},
                {"BM", "Bermuda", "HIGH"},
                {"VG", "British Virgin Islands", "HIGH"},
                {"PA", "Panama", "HIGH"},
                {"BS", "Bahamas", "MEDIUM"},

                // High Risk - Other Concerns
                {"AF", "Afghanistan", "CRITICAL"},
                {"SO", "Somalia", "CRITICAL"},
                {"LY", "Libya", "HIGH"},
                {"IQ", "Iraq", "HIGH"},
                {"VE", "Venezuela", "HIGH"},
                {"ZW", "Zimbabwe", "MEDIUM"},

                // Enhanced Due Diligence
                {"RU", "Russia", "MEDIUM"},
                {"CN", "China", "MEDIUM"},
                {"HK", "Hong Kong", "MEDIUM"}
        };

        for (String[] countryData : countries) {
            RiskyCountry country = new RiskyCountry();
            country.setCountryCode(countryData[0]);
            country.setCountryName(countryData[1]);
            country.setRiskLevel(RiskLevel.valueOf(countryData[2]));
            countryRepository.save(country);
        }
        logger.info("✓ {} Risky Countries created", countries.length);
    }
}
