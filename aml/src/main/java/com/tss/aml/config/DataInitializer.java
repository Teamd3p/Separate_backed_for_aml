package com.tss.aml.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tss.aml.entity.Account;
import com.tss.aml.entity.Admin;
import com.tss.aml.entity.ComplianceOfficer;
import com.tss.aml.entity.CurrencyExchange;
import com.tss.aml.entity.Customer;
import com.tss.aml.entity.KycDocument;
import com.tss.aml.entity.RiskyCountry;
import com.tss.aml.entity.Rule;
import com.tss.aml.entity.SuspiciousKeyword;
import com.tss.aml.entity.enums.AccountStatus;
import com.tss.aml.entity.enums.AccountType;
import com.tss.aml.entity.enums.DocumentType;
import com.tss.aml.entity.enums.KycStatus;
import com.tss.aml.entity.enums.RateSource;
import com.tss.aml.entity.enums.RiskLevel;
import com.tss.aml.entity.enums.Role;
import com.tss.aml.entity.enums.RuleType;
import com.tss.aml.entity.enums.UserStatus;
import com.tss.aml.repository.AccountRepository;
import com.tss.aml.repository.ComplianceOfficerRepository;
import com.tss.aml.repository.CurrencyExchangeRepository;
import com.tss.aml.repository.CustomerRepository;
import com.tss.aml.repository.KycDocumentRepository;
import com.tss.aml.repository.RiskyCountryRepository;
import com.tss.aml.repository.RuleRepository;
import com.tss.aml.repository.SuspiciousKeywordRepository;
import com.tss.aml.repository.AdminRepository;
import com.tss.aml.repository.UserRepository;

@Configuration
public class DataInitializer {

	private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository, AdminRepository adminRepository, 
			ComplianceOfficerRepository officerRepository, CustomerRepository customerRepository, 
			AccountRepository accountRepository, KycDocumentRepository kycDocumentRepository, 
			RuleRepository ruleRepository, SuspiciousKeywordRepository keywordRepository, 
			RiskyCountryRepository countryRepository, CurrencyExchangeRepository currencyExchangeRepository, 
			PasswordEncoder passwordEncoder) {

		return args -> {
			logger.info("🚀 Starting comprehensive AML data initialization...");

			// Check if data already exists
			if (userRepository.count() > 1) {
				logger.info("✅ Data already exists, skipping initialization");
				return;
			}

			// Initialize Admin Users
			initializeAdmins(adminRepository, passwordEncoder);

			// Initialize Compliance Officers
			initializeComplianceOfficers(officerRepository, passwordEncoder);

			// Initialize Customers
			initializeCustomers(customerRepository, passwordEncoder);

			// Initialize Accounts
			initializeAccounts(accountRepository, customerRepository);

			// Initialize KYC Documents
			initializeKycDocuments(kycDocumentRepository, customerRepository);

			// Initialize Advanced AML Rules
			initializeAdvancedAMLRules(ruleRepository);

			// Initialize Comprehensive Suspicious Keywords
			initializeComprehensiveSuspiciousKeywords(keywordRepository);

			// Initialize Enhanced Risky Countries
			initializeEnhancedRiskyCountries(countryRepository);

			// Initialize Currency Exchange Rates
			initializeCurrencyExchangeRates(currencyExchangeRepository);

			logger.info("✅ Comprehensive AML data initialization completed successfully!");
		};
	}

	private void initializeAdmins(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
		logger.info("👤 Initializing Admin Users...");

		String[][] adminData = {
				{ "admin@aml.com", "System", "Administrator", "9999999999", "EMP001", "Administration" },
				{ "john.admin@aml.com", "John", "Smith", "9999999998", "EMP002", "IT Administration" },
				{ "sarah.admin@aml.com", "Sarah", "Johnson", "9999999997", "EMP003", "Risk Management" } };

		int created = 0;
		for (String[] data : adminData) {
			if (!adminRepository.findByEmail(data[0]).isPresent()) {
				Admin admin = new Admin();
				admin.setEmail(data[0]);
				admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
				admin.setRole(Role.ADMIN);
				admin.setStatus(UserStatus.ACTIVE);
				admin.setEmailVerified(true);
				admin.setFirstName(data[1]);
				admin.setLastName(data[2]);
				admin.setPhone(data[3]);

				adminRepository.save(admin);
				created++;
			}
		}
		logger.info("✓ Admin Users: {} created, {} already exist", created, adminData.length - created);
	}

	private void initializeComplianceOfficers(ComplianceOfficerRepository officerRepository,
			PasswordEncoder passwordEncoder) {
		logger.info("👮 Initializing Compliance Officers...");

		String[][] officers = {
				{ "sarah.williams@aml.com", "Sarah", "Williams", "9876543211", "CO001", "CAMS, CFE", "Senior" },
				{ "john.smith@aml.com", "John", "Smith", "9876543212", "CO002", "CAMS, ACAMS", "Mid-Level" },
				{ "emily.chen@aml.com", "Emily", "Chen", "9876543213", "CO003", "CFE, ACFCS", "Junior" },
				{ "michael.brown@aml.com", "Michael", "Brown", "9876543214", "CO004", "CAMS, CGSS", "Senior" },
				{ "lisa.davis@aml.com", "Lisa", "Davis", "9876543215", "CO005", "ACAMS, CFE", "Mid-Level" } };

		int created = 0;
		for (String[] officerData : officers) {
			if (!officerRepository.findByEmail(officerData[0]).isPresent()) {
				ComplianceOfficer officer = new ComplianceOfficer();
				officer.setEmail(officerData[0]);
				officer.setPasswordHash(passwordEncoder.encode("Officer@123"));
				officer.setRole(Role.COMPLIANCE_OFFICER);
				officer.setStatus(UserStatus.ACTIVE);
				officer.setEmailVerified(true);
				officer.setFirstName(officerData[1]);
				officer.setLastName(officerData[2]);
				officer.setPhone(officerData[3]);
				
				officer.setCreatedAt(LocalDateTime.now());

				officerRepository.save(officer);
				created++;
			}
		}
		logger.info("✓ Compliance Officers: {} created, {} already exist", created, officers.length - created);
	}

	private void initializeCustomers(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
		logger.info("👥 Initializing Customers...");

		Object[][] customerData = {
				{ "alice.johnson@example.com", "Alice", "Johnson", LocalDate.of(1990, 5, 15), "Indian", "9876543210",
						KycStatus.VERIFIED, "123 MG Road", "Mumbai", "Maharashtra", "India", "400001" },
				{ "bob.smith@example.com", "Bob", "Smith", LocalDate.of(1985, 8, 22), "American", "9876543211",
						KycStatus.PENDING, "456 Park Avenue", "Delhi", "Delhi", "India", "110001" },
				{ "charlie.brown@example.com", "Charlie", "Brown", LocalDate.of(1992, 12, 3), "British", "9876543212",
						KycStatus.REJECTED, "789 Brigade Road", "Bangalore", "Karnataka", "India", "560001" },
				{ "diana.wilson@example.com", "Diana", "Wilson", LocalDate.of(1988, 3, 18), "Canadian", "9876543213",
						KycStatus.VERIFIED, "321 Marine Drive", "Mumbai", "Maharashtra", "India", "400002" },
				{ "eve.davis@example.com", "Eve", "Davis", LocalDate.of(1995, 7, 9), "Australian", "9876543214",
						KycStatus.PENDING, "654 Connaught Place", "Delhi", "Delhi", "India", "110002" },
				{ "frank.miller@example.com", "Frank", "Miller", LocalDate.of(1982, 11, 25), "German", "9876543215",
						KycStatus.VERIFIED, "987 Commercial Street", "Bangalore", "Karnataka", "India", "560002" },
				{ "grace.lee@example.com", "Grace", "Lee", LocalDate.of(1993, 4, 12), "Korean", "9876543216",
						KycStatus.MANUAL_REVIEW, "147 Linking Road", "Mumbai", "Maharashtra", "India", "400003" },
				{ "henry.garcia@example.com", "Henry", "Garcia", LocalDate.of(1987, 9, 30), "Spanish", "9876543217",
						KycStatus.VERIFIED, "258 Khan Market", "Delhi", "Delhi", "India", "110003" },
				{ "isabel.martinez@example.com", "Isabel", "Martinez", LocalDate.of(1991, 1, 8), "Mexican",
						"9876543218", KycStatus.PENDING, "369 Residency Road", "Bangalore", "Karnataka", "India", "560003" },
				{ "jack.anderson@example.com", "Jack", "Anderson", LocalDate.of(1984, 6, 17), "Swedish", "9876543219",
						KycStatus.VERIFIED, "741 Bandra West", "Mumbai", "Maharashtra", "India", "400004" } };

		int created = 0;
		for (Object[] data : customerData) {
			if (!customerRepository.findByEmail((String) data[0]).isPresent()) {
				Customer customer = new Customer();
				customer.setEmail((String) data[0]);
				customer.setPasswordHash(passwordEncoder.encode("Customer@123"));
				customer.setRole(Role.CUSTOMER);
				customer.setStatus(UserStatus.ACTIVE);
				customer.setEmailVerified(true);
				customer.setFirstName((String) data[1]);
				customer.setLastName((String) data[2]);
				customer.setDateOfBirth((LocalDate) data[3]);
				customer.setNationality((String) data[4]);
				customer.setContactNumber((String) data[5]);
				customer.setKycStatus((KycStatus) data[6]);
				customer.setStreet((String) data[7]);
				customer.setCity((String) data[8]);
				customer.setState((String) data[9]);
				customer.setNation((String) data[10]);
				customer.setPincode((String) data[11]);
				customer.setCreatedAt(LocalDateTime.now());

				customerRepository.save(customer);
				created++;
			}
		}
		logger.info("✓ Customers: {} created, {} already exist", created, customerData.length - created);
	}

	private void initializeAccounts(AccountRepository accountRepository, CustomerRepository customerRepository) {
		logger.info("🏦 Initializing Accounts...");

		var customers = customerRepository.findAll();
		if (customers.isEmpty()) {
			logger.warn("No customers found, skipping account initialization");
			return;
		}

		Object[][] accountData = {
				{ 0, "ACC001001", AccountType.SAVING, new BigDecimal("50000.00"), AccountStatus.ACTIVE, "INR" },
				{ 0, "ACC001002", AccountType.CURRENT, new BigDecimal("100000.00"), AccountStatus.ACTIVE, "USD" },
				{ 1, "ACC002001", AccountType.SAVING, new BigDecimal("25000.00"), AccountStatus.ACTIVE, "INR" },
				{ 1, "ACC002002", AccountType.SALARY, new BigDecimal("75000.00"), AccountStatus.ACTIVE, "INR" },
				{ 2, "ACC003001", AccountType.CURRENT, new BigDecimal("75000.00"), AccountStatus.FROZEN, "EUR" },
				{ 3, "ACC004001", AccountType.SALARY, new BigDecimal("80000.00"), AccountStatus.ACTIVE, "INR" },
				{ 3, "ACC004002", AccountType.SAVING, new BigDecimal("120000.00"), AccountStatus.ACTIVE, "USD" },
				{ 4, "ACC005001", AccountType.SAVING, new BigDecimal("30000.00"), AccountStatus.PENDING, "INR" },
				{ 5, "ACC006001", AccountType.CURRENT, new BigDecimal("200000.00"), AccountStatus.ACTIVE, "EUR" },
				{ 6, "ACC007001", AccountType.SAVING, new BigDecimal("45000.00"), AccountStatus.ACTIVE, "INR" },
				{ 7, "ACC008001", AccountType.SALARY, new BigDecimal("95000.00"), AccountStatus.ACTIVE, "USD" },
				{ 8, "ACC009001", AccountType.CURRENT, new BigDecimal("65000.00"), AccountStatus.ACTIVE, "INR" },
				{ 9, "ACC010001", AccountType.SAVING, new BigDecimal("500000.00"), AccountStatus.ACTIVE, "USD" } };

		int created = 0;
		for (Object[] data : accountData) {
			int customerIndex = (Integer) data[0];
			if (customerIndex < customers.size()) {
				if (accountRepository.findByAccountNumber((String) data[1]) == null) {
					Account account = new Account();
					account.setCustomer(customers.get(customerIndex));
					account.setAccountNumber((String) data[1]);
					account.setAccountType((AccountType) data[2]);
					account.setBalance((BigDecimal) data[3]);
					account.setStatus((AccountStatus) data[4]);
					account.setCurrency((String) data[5]);
					account.setCreatedAt(LocalDateTime.now());

					accountRepository.save(account);
					created++;
				}
			}
		}
		logger.info("✓ Accounts: {} created", created);
	}

	private void initializeKycDocuments(KycDocumentRepository kycDocumentRepository,
			CustomerRepository customerRepository) {
		logger.info("📄 Initializing KYC Documents...");

		var customers = customerRepository.findAll();
		if (customers.isEmpty()) {
			logger.warn("No customers found, skipping KYC document initialization");
			return;
		}

		Object[][] docData = { { 0, DocumentType.PAN, "alice_pan.pdf", KycStatus.VERIFIED, 15, 95 },
				{ 0, DocumentType.AADHAAR, "alice_aadhaar.pdf", KycStatus.VERIFIED, 20, 92 },
				{ 1, DocumentType.PASSPORT, "bob_passport.pdf", KycStatus.PENDING, 45, 78 },
				{ 1, DocumentType.PAN, "bob_pan.pdf", KycStatus.PENDING, 50, 75 },
				{ 2, DocumentType.DRIVING_LICENSE, "charlie_dl.pdf", KycStatus.REJECTED, 85, 45 },
				{ 3, DocumentType.VOTER_ID, "diana_voter.pdf", KycStatus.VERIFIED, 25, 88 },
				{ 3, DocumentType.PAN, "diana_pan.pdf", KycStatus.VERIFIED, 18, 94 },
				{ 4, DocumentType.PAN, "eve_pan.pdf", KycStatus.PENDING, 60, 65 },
				{ 5, DocumentType.PASSPORT, "frank_passport.pdf", KycStatus.VERIFIED, 22, 90 },
				{ 6, DocumentType.AADHAAR, "grace_aadhaar.pdf", KycStatus.MANUAL_REVIEW, 75, 55 },
				{ 7, DocumentType.DRIVING_LICENSE, "henry_dl.pdf", KycStatus.VERIFIED, 30, 85 },
				{ 8, DocumentType.VOTER_ID, "isabel_voter.pdf", KycStatus.PENDING, 40, 70 },
				{ 9, DocumentType.BANK_STATEMENT, "jack_statement.pdf", KycStatus.VERIFIED, 95, 35 } };

		int created = 0;
		for (Object[] data : docData) {
			int customerIndex = (Integer) data[0];
			if (customerIndex < customers.size()) {
				KycDocument doc = new KycDocument();
				doc.setCustomer(customers.get(customerIndex));
				doc.setDocType((DocumentType) data[1]);
				doc.setFileName((String) data[2]);
				doc.setFileUrl("/uploads/kyc-documents/" + data[2]);
				doc.setStatus((KycStatus) data[3]);
				doc.setRiskScore((Integer) data[4]);
			
				doc.setUploadTimestamp(LocalDateTime.now());

				kycDocumentRepository.save(doc);
				created++;
			}
		}
		logger.info("✓ KYC Documents: {} created", created);
	}

	private void initializeAdvancedAMLRules(RuleRepository ruleRepository) {
		logger.info("📋 Initializing Advanced AML Rules...");

		if (ruleRepository.count() > 0) {
			logger.info("✓ AML Rules already exist ({} rules)", ruleRepository.count());
			return;
		}

		Rule[] rules = {
				// Geographic Risk Rules
				createRule("Critical Risk Country Block",
						"Block or flag transactions to/from countries on the critical risk list.", RuleType.GEOGRAPHIC,
						"{\"riskType\": \"CRITICAL\", \"listName\": \"critical_country_list\"}", 95),

				createRule("High-Risk FX Corridor Transfer (Enhanced)",
						"Flags transfers on risky corridors and escalates risk based on a user's repeated activity with medium-risk countries.",
						RuleType.GEOGRAPHIC,
						"{\"riskCorridorsListName\": \"fx_corridor_list\", \"mediumRiskHistory\": {\"countryRiskLevel\": \"MEDIUM\", \"lookbackDays\": 90, \"transactionCount\": 2}}",
						85),

				// Threshold-Based Rules
				createRule("High-Value Transfer (Account-Type Specific)",
						"Applies dynamic, account-type-specific thresholds for large transactions, checking amounts, frequency, and history.",
						RuleType.THRESHOLD,
						"{\"baselineCurrency\": \"INR\", \"accountTypeRules\": {\"CURRENT\": {\"dailyLimitCount\": 1, \"amountThreshold\": 1000000, \"historyMultiplier\": 3.0}, \"SAVING\": {\"windowHours\": 24, \"transactionCountThreshold\": 3, \"amountThreshold\": 100000, \"initialAllowedAmount\": 250000}, \"SALARY\": {\"historyMultiplier\": 2.0, \"allowFirstTransaction\": true}}}",
						65),

				// Pattern Detection Rules
				createRule("Structuring/Smurfing Detection",
						"Detects smurfing via multiple small deposits (structuring) or micro-bursts of small transfers in a short time.",
						RuleType.PATTERN,
						"{\"structuringPattern\": {\"timeWindowMinutes\": 1440, \"transactionType\": \"CREDIT\"}, \"microburstPattern\": {\"timeWindowMinutes\": 5, \"minTransactions\": 3, \"transactionType\": \"DEBIT\"}}",
						90),

				createRule("Rapid Deposit-Transfer Pattern",
						"Detects 'pass-through' activity (muling) where a deposit is quickly transferred out.",
						RuleType.VELOCITY,
						"{\"timeWindowMinutes\": 60, \"minAmountUSD\": 5000, \"amountMatchPercentage\": 90}", 80),

				createRule("Salary Account Pass-Through",
						"Detects when a salary is credited and almost immediately transferred out in full, a strong indicator of mule activity.",
						RuleType.PATTERN,
						"{\"accountType\": \"SALARY\", \"payoutPercentage\": 90, \"timeWindowHours\": 48}", 85),

				// Frequency-Based Rules
				createRule("Rapid Withdrawals (Account-Type Specific)",
						"Flags unusually fast or large withdrawals based on account-specific thresholds, referencing the logic from the High-Value Transfer rule.",
						RuleType.FREQUENCY,
						"{\"referenceRule\": \"High-Value Transfer (Account-Type Specific)\", \"transactionTypes\": [\"DEBIT\", \"ATM_WITHDRAWAL\"]}",
						75),

				createRule("High Transfer Velocity",
						"Monitors for an unusually high number of significant transfers compared to the account's normal behavior.",
						RuleType.FREQUENCY,
						"{\"maxTransactions\": 5, \"timeWindowMinutes\": 120, \"minAmountUSD\": 2000, \"checkHistoryDeviation\": true}",
						80),

				// Behavioral Rules
				createRule("Dormant Account Activation",
						"Flags any activity on a long-dormant account, with special checks for prior flagged history.",
						RuleType.BEHAVIOR, "{\"dormantDays\": 180, \"minAmountUSD\": 1, \"checkFlaggedHistory\": true}",
						90),

				createRule("Multi-Currency Profile Activity",
						"Applies stricter monitoring and potentially different thresholds to users who hold accounts in more than 3 currencies.",
						RuleType.BEHAVIOR,
						"{\"profileTrigger\": {\"minCurrencyAccounts\": 3}, \"specialThresholds\": {\"internationalTransferLimitUSD\": 5000}}",
						65),

				createRule("Use of Multiple High-Fee FX Conversions",
						"Flags users who frequently perform FX conversions with unusually high fees, suggesting cost is not a concern.",
						RuleType.BEHAVIOR,
						"{\"feePercentageThreshold\": 2.5, \"minTransactions\": 3, \"timeWindowHours\": 72}", 70),

				createRule("Unusual Business Hours Activity (Current Account)",
						"Flags significant activity in a business (Current) account outside of standard 9-to-5 business hours.",
						RuleType.BEHAVIOR,
						"{\"accountType\": \"CURRENT\", \"timeWindow\": \"22:00-06:00\", \"minAmountUSD\": 5000}", 70),

				// Velocity Rules
				createRule("Rapid Currency Cycling",
						"Detects rapid fund conversion through multiple currencies in a short time, a common layering technique.",
						RuleType.VELOCITY, "{\"minDistinctCurrencies\": 3, \"timeWindowMinutes\": 60}", 75),

				// Graph-Based Rules
				createRule("Fan-In / Fan-Out Pattern (Graph)",
						"Identifies central accounts receiving from many sources (Fan-In) or sending to many destinations (Fan-Out).",
						RuleType.GRAPH_PATTERN,
						"{\"pattern\": [\"FAN_IN\", \"FAN_OUT\"], \"timeWindowHours\": 24, \"minEdgeCount\": 5}", 90),

				createRule("Newly Connected High-Risk Entity (Graph)",
						"Flags transactions that create a new connection to a blacklisted or known high-risk entity.",
						RuleType.GRAPH_BEHAVIOR,
						"{\"trigger\": \"NEW_CONNECTION\", \"targetRiskScore\": 90, \"targetLists\": [\"internal_blacklist\", \"sanctions_list\"]}",
						95),

				// Additional Standard Rules
				createRule("High Value Transaction - Critical", "Flag transactions above 500,000 INR as critical risk",
						RuleType.THRESHOLD,
						"{\"threshold\": 500000, \"currency\": \"INR\", \"operator\": \"GREATER_THAN\"}", 80),

				createRule("Round Amount Structuring", "Flag multiple round amount transactions (possible structuring)",
						RuleType.PATTERN, "{\"threshold\": 5, \"timeWindow\": \"1_DAY\", \"roundTo\": 1000}", 55) };

		for (Rule rule : rules) {
			ruleRepository.save(rule);
		}
		logger.info("✓ {} Advanced AML Rules created", rules.length);
	}

	private Rule createRule(String name, String description, RuleType type, String conditions, int riskScore) {
		Rule rule = new Rule();
		rule.setName(name);
		rule.setDescription(description);
		rule.setType(type);
		rule.setConditions(conditions);
		rule.setRiskScoreImpact(riskScore);
		rule.setActive(true);
		rule.setCreatedAt(LocalDateTime.now());
		return rule;
	}

	private void initializeComprehensiveSuspiciousKeywords(SuspiciousKeywordRepository keywordRepository) {
		logger.info("🔍 Initializing Comprehensive Suspicious Keywords...");

		if (keywordRepository.count() > 0) {
			logger.info("✓ Suspicious Keywords already exist ({} keywords)", keywordRepository.count());
			return;
		}

		String[][] keywords = {
				// Money Laundering & Financial Crime
				{ "laundering", "MONEY_LAUNDERING", "10" }, { "money laundering", "MONEY_LAUNDERING", "10" },
				{ "wash money", "MONEY_LAUNDERING", "9" }, { "clean money", "MONEY_LAUNDERING", "9" },
				{ "dirty money", "MONEY_LAUNDERING", "9" }, { "placement", "MONEY_LAUNDERING", "8" },
				{ "layering", "MONEY_LAUNDERING", "8" }, { "integration", "MONEY_LAUNDERING", "8" },
				{ "smurfing", "MONEY_LAUNDERING", "9" }, { "structuring", "MONEY_LAUNDERING", "8" },
				{ "cash intensive", "MONEY_LAUNDERING", "7" }, { "bulk cash", "MONEY_LAUNDERING", "8" },
				{ "currency exchange", "MONEY_LAUNDERING", "6" }, { "round tripping", "MONEY_LAUNDERING", "7" },

				// Hawala & Alternative Remittance
				{ "hawala", "ALTERNATIVE_REMITTANCE", "9" }, { "hundi", "ALTERNATIVE_REMITTANCE", "9" },
				{ "fei chien", "ALTERNATIVE_REMITTANCE", "8" }, { "padala", "ALTERNATIVE_REMITTANCE", "8" },
				{ "informal transfer", "ALTERNATIVE_REMITTANCE", "7" },
				{ "underground banking", "ALTERNATIVE_REMITTANCE", "9" },
				{ "value transfer", "ALTERNATIVE_REMITTANCE", "6" },

				// Shell Companies & Corporate Structures
				{ "shell company", "CORPORATE_STRUCTURES", "8" }, { "nominee director", "CORPORATE_STRUCTURES", "7" },
				{ "bearer shares", "CORPORATE_STRUCTURES", "9" }, { "shelf company", "CORPORATE_STRUCTURES", "8" },
				{ "front company", "CORPORATE_STRUCTURES", "8" }, { "paper company", "CORPORATE_STRUCTURES", "8" },
				{ "letterbox company", "CORPORATE_STRUCTURES", "7" },
				{ "beneficial owner", "CORPORATE_STRUCTURES", "6" },
				{ "ultimate beneficial owner", "CORPORATE_STRUCTURES", "6" },

				// Offshore & Tax Havens
				{ "offshore", "OFFSHORE", "7" }, { "tax haven", "OFFSHORE", "8" },
				{ "secrecy jurisdiction", "OFFSHORE", "8" }, { "panama papers", "OFFSHORE", "9" },
				{ "paradise papers", "OFFSHORE", "9" }, { "cayman islands", "OFFSHORE", "7" },
				{ "british virgin islands", "OFFSHORE", "7" }, { "bermuda", "OFFSHORE", "6" },
				{ "luxembourg", "OFFSHORE", "5" }, { "singapore banking", "OFFSHORE", "6" },

				// Terrorism Financing
				{ "terrorism", "TERRORISM_FINANCING", "10" }, { "terrorist", "TERRORISM_FINANCING", "10" },
				{ "terror funding", "TERRORISM_FINANCING", "10" }, { "jihad", "TERRORISM_FINANCING", "9" },
				{ "isis", "TERRORISM_FINANCING", "10" }, { "al qaeda", "TERRORISM_FINANCING", "10" },
				{ "taliban", "TERRORISM_FINANCING", "10" }, { "charity fraud", "TERRORISM_FINANCING", "8" },
				{ "fake charity", "TERRORISM_FINANCING", "9" }, { "religious donation", "TERRORISM_FINANCING", "6" },

				// Drug Trafficking
				{ "narcotics", "DRUG_TRAFFICKING", "9" }, { "drug money", "DRUG_TRAFFICKING", "10" },
				{ "cocaine", "DRUG_TRAFFICKING", "9" }, { "heroin", "DRUG_TRAFFICKING", "9" },
				{ "fentanyl", "DRUG_TRAFFICKING", "10" }, { "cartel", "DRUG_TRAFFICKING", "9" },
				{ "drug proceeds", "DRUG_TRAFFICKING", "9" }, { "methamphetamine", "DRUG_TRAFFICKING", "9" },
				{ "opium", "DRUG_TRAFFICKING", "8" },

				// Fraud & Scams
				{ "ponzi", "FRAUD", "9" }, { "pyramid scheme", "FRAUD", "9" }, { "advance fee fraud", "FRAUD", "8" },
				{ "romance scam", "FRAUD", "7" }, { "investment fraud", "FRAUD", "8" },
				{ "fake invoice", "FRAUD", "8" }, { "false documentation", "FRAUD", "8" },
				{ "identity theft", "FRAUD", "8" }, { "credit card fraud", "FRAUD", "7" },
				{ "wire fraud", "FRAUD", "8" }, { "check kiting", "FRAUD", "7" },

				// Tax Evasion
				{ "tax evasion", "TAX_EVASION", "8" }, { "evade tax", "TAX_EVASION", "8" },
				{ "undeclared income", "TAX_EVASION", "7" }, { "black money", "TAX_EVASION", "9" },
				{ "unreported income", "TAX_EVASION", "7" }, { "tax avoidance", "TAX_EVASION", "6" },
				{ "transfer pricing", "TAX_EVASION", "6" }, { "double irish", "TAX_EVASION", "7" },

				// Corruption & Bribery
				{ "bribe", "CORRUPTION", "9" }, { "kickback", "CORRUPTION", "8" }, { "corruption", "CORRUPTION", "8" },
				{ "embezzlement", "CORRUPTION", "9" }, { "political donation", "CORRUPTION", "6" },
				{ "facilitation payment", "CORRUPTION", "7" }, { "grease payment", "CORRUPTION", "7" },
				{ "slush fund", "CORRUPTION", "8" },

				// Human Trafficking
				{ "human trafficking", "HUMAN_TRAFFICKING", "10" }, { "sex trafficking", "HUMAN_TRAFFICKING", "10" },
				{ "forced labor", "HUMAN_TRAFFICKING", "9" }, { "modern slavery", "HUMAN_TRAFFICKING", "9" },
				{ "smuggling people", "HUMAN_TRAFFICKING", "9" }, { "labor exploitation", "HUMAN_TRAFFICKING", "8" },

				// Weapons & Arms
				{ "arms deal", "WEAPONS", "9" }, { "weapon sale", "WEAPONS", "9" }, { "illegal arms", "WEAPONS", "10" },
				{ "gun running", "WEAPONS", "9" }, { "arms trafficking", "WEAPONS", "10" },
				{ "weapons smuggling", "WEAPONS", "9" },

				// Sanctions Evasion
				{ "sanctions evasion", "SANCTIONS", "10" }, { "embargo", "SANCTIONS", "8" },
				{ "blocked person", "SANCTIONS", "9" }, { "designated person", "SANCTIONS", "9" },
				{ "ofac", "SANCTIONS", "8" }, { "specially designated national", "SANCTIONS", "9" },

				// Trade-Based Money Laundering
				{ "over invoicing", "TRADE_BASED", "8" }, { "under invoicing", "TRADE_BASED", "8" },
				{ "multiple invoicing", "TRADE_BASED", "8" }, { "phantom shipment", "TRADE_BASED", "9" },
				{ "trade mispricing", "TRADE_BASED", "7" }, { "invoice manipulation", "TRADE_BASED", "8" },

				// Cryptocurrency & Digital Assets
				{ "bitcoin laundering", "CRYPTOCURRENCY", "8" }, { "crypto mixing", "CRYPTOCURRENCY", "9" },
				{ "tumbler", "CRYPTOCURRENCY", "9" }, { "privacy coin", "CRYPTOCURRENCY", "7" },
				{ "dark web", "CRYPTOCURRENCY", "8" }, { "ransomware", "CRYPTOCURRENCY", "9" },

				// Suspicious Behavior Indicators
				{ "cash only", "SUSPICIOUS_BEHAVIOR", "5" }, { "no questions", "SUSPICIOUS_BEHAVIOR", "6" },
				{ "urgent transfer", "SUSPICIOUS_BEHAVIOR", "5" }, { "anonymous", "SUSPICIOUS_BEHAVIOR", "7" },
				{ "untraceable", "SUSPICIOUS_BEHAVIOR", "8" }, { "quick transaction", "SUSPICIOUS_BEHAVIOR", "5" },
				{ "avoid reporting", "SUSPICIOUS_BEHAVIOR", "8" }, { "split transaction", "SUSPICIOUS_BEHAVIOR", "7" },
				{ "round amount", "SUSPICIOUS_BEHAVIOR", "6" }, { "just under limit", "SUSPICIOUS_BEHAVIOR", "7" },
				{ "frequent small amounts", "SUSPICIOUS_BEHAVIOR", "6" },
				{ "unusual pattern", "SUSPICIOUS_BEHAVIOR", "5" }, { "high risk customer", "SUSPICIOUS_BEHAVIOR", "6" },
				{ "politically exposed person", "SUSPICIOUS_BEHAVIOR", "7" }, { "pep", "SUSPICIOUS_BEHAVIOR", "7" } };

		for (String[] keywordData : keywords) {
			SuspiciousKeyword keyword = new SuspiciousKeyword();
			keyword.setWord(keywordData[0]);
			keyword.setCategory(keywordData[1]);
			keyword.setSeverity(Integer.parseInt(keywordData[2]));
			keyword.setActive(true);
			keywordRepository.save(keyword);
		}
		logger.info("✓ {} Comprehensive Suspicious Keywords created", keywords.length);
	}

	private void initializeEnhancedRiskyCountries(RiskyCountryRepository countryRepository) {
		logger.info("🌍 Initializing Enhanced Risky Countries...");

		if (countryRepository.count() > 0) {
			logger.info("✓ Risky Countries already exist ({} countries)", countryRepository.count());
			return;
		}

		String[][] countries = {
				// FATF Black List - Critical Risk
				{ "KP", "North Korea", "CRITICAL" }, { "IR", "Iran", "CRITICAL" }, { "AF", "Afghanistan", "CRITICAL" },
				{ "SO", "Somalia", "CRITICAL" },

				// FATF Grey List & High Risk Countries
				{ "SY", "Syria", "HIGH" }, { "YE", "Yemen", "HIGH" }, { "MM", "Myanmar", "HIGH" },
				{ "LY", "Libya", "HIGH" }, { "IQ", "Iraq", "HIGH" }, { "VE", "Venezuela", "HIGH" },
				{ "ML", "Mali", "HIGH" }, { "BF", "Burkina Faso", "HIGH" }, { "HT", "Haiti", "HIGH" },
				{ "JO", "Jordan", "HIGH" }, { "TR", "Turkey", "HIGH" },

				// Tax Havens & Offshore Centers
				{ "KY", "Cayman Islands", "HIGH" }, { "BM", "Bermuda", "HIGH" },
				{ "VG", "British Virgin Islands", "HIGH" }, { "PA", "Panama", "HIGH" },
				{ "CH", "Switzerland", "MEDIUM" }, { "LU", "Luxembourg", "MEDIUM" },
				{ "LI", "Liechtenstein", "MEDIUM" }, { "AD", "Andorra", "MEDIUM" }, { "MC", "Monaco", "MEDIUM" },
				{ "BS", "Bahamas", "MEDIUM" }, { "BB", "Barbados", "MEDIUM" },

				// Enhanced Due Diligence Countries
				{ "PK", "Pakistan", "MEDIUM" }, { "BD", "Bangladesh", "MEDIUM" }, { "JM", "Jamaica", "MEDIUM" },
				{ "UG", "Uganda", "MEDIUM" }, { "PH", "Philippines", "MEDIUM" }, { "ZW", "Zimbabwe", "MEDIUM" },
				{ "RU", "Russia", "MEDIUM" }, { "BY", "Belarus", "MEDIUM" }, { "CN", "China", "MEDIUM" },
				{ "HK", "Hong Kong", "MEDIUM" }, { "MO", "Macau", "MEDIUM" },

				// Sanctions & Embargoed Countries
				{ "CU", "Cuba", "HIGH" }, { "SD", "Sudan", "HIGH" }, { "SS", "South Sudan", "HIGH" },
				{ "CF", "Central African Republic", "HIGH" }, { "CD", "Democratic Republic of Congo", "MEDIUM" },
				{ "ER", "Eritrea", "MEDIUM" }, { "GW", "Guinea-Bissau", "MEDIUM" }, { "LB", "Lebanon", "MEDIUM" },
				{ "LR", "Liberia", "MEDIUM" },

				// Conflict Zones & Political Instability
				{ "ET", "Ethiopia", "MEDIUM" }, { "NI", "Nicaragua", "MEDIUM" }, { "MZ", "Mozambique", "MEDIUM" },
				{ "TD", "Chad", "MEDIUM" }, { "NE", "Niger", "MEDIUM" }, { "CM", "Cameroon", "MEDIUM" },

				// Cryptocurrency & Digital Asset Risks
				{ "SV", "El Salvador", "MEDIUM" }, { "UA", "Ukraine", "MEDIUM" },

				// Border Security & Smuggling Concerns
				{ "MX", "Mexico", "MEDIUM" }, { "CO", "Colombia", "MEDIUM" }, { "PE", "Peru", "MEDIUM" },
				{ "BO", "Bolivia", "MEDIUM" },

				// Financial Secrecy & Banking Concerns
				{ "MT", "Malta", "MEDIUM" }, { "CY", "Cyprus", "MEDIUM" }, { "LV", "Latvia", "MEDIUM" },
				{ "EE", "Estonia", "MEDIUM" } };

		for (String[] countryData : countries) {
			RiskyCountry country = new RiskyCountry();
			country.setCountryCode(countryData[0]);
			country.setCountryName(countryData[1]);
			country.setRiskLevel(RiskLevel.valueOf(countryData[2]));
			countryRepository.save(country);
		}
		logger.info("✓ {} Enhanced Risky Countries created", countries.length);
	}

	private void initializeCurrencyExchangeRates(CurrencyExchangeRepository currencyExchangeRepository) {
		logger.info("💱 Initializing Currency Exchange Rates...");

		if (currencyExchangeRepository.count() > 0) {
			logger.info("✓ Currency Exchange Rates already exist ({} rates)", currencyExchangeRepository.count());
			return;
		}

		Object[][] exchangeRates = {
				// Major Currency Pairs
				{ "USD", "INR", new BigDecimal("83.25"), new BigDecimal("2.50"), new BigDecimal("5.00"),
						new BigDecimal("500.00") },
				{ "EUR", "INR", new BigDecimal("90.45"), new BigDecimal("2.75"), new BigDecimal("5.50"),
						new BigDecimal("550.00") },
				{ "GBP", "INR", new BigDecimal("105.30"), new BigDecimal("3.00"), new BigDecimal("6.00"),
						new BigDecimal("600.00") },
				{ "JPY", "INR", new BigDecimal("0.56"), new BigDecimal("2.25"), new BigDecimal("4.50"),
						new BigDecimal("450.00") },
				{ "AUD", "INR", new BigDecimal("55.75"), new BigDecimal("2.80"), new BigDecimal("5.60"),
						new BigDecimal("560.00") },
				{ "CAD", "INR", new BigDecimal("61.20"), new BigDecimal("2.60"), new BigDecimal("5.20"),
						new BigDecimal("520.00") },
				{ "CHF", "INR", new BigDecimal("92.80"), new BigDecimal("3.25"), new BigDecimal("6.50"),
						new BigDecimal("650.00") },
				{ "CNY", "INR", new BigDecimal("11.45"), new BigDecimal("2.40"), new BigDecimal("4.80"),
						new BigDecimal("480.00") },

				// Cross Currency Pairs
				{ "USD", "EUR", new BigDecimal("0.92"), new BigDecimal("1.50"), new BigDecimal("3.00"),
						new BigDecimal("300.00") },
				{ "USD", "GBP", new BigDecimal("0.79"), new BigDecimal("1.75"), new BigDecimal("3.50"),
						new BigDecimal("350.00") },
				{ "USD", "JPY", new BigDecimal("149.50"), new BigDecimal("1.25"), new BigDecimal("2.50"),
						new BigDecimal("250.00") },
				{ "USD", "AUD", new BigDecimal("1.49"), new BigDecimal("1.80"), new BigDecimal("3.60"),
						new BigDecimal("360.00") },
				{ "USD", "CAD", new BigDecimal("1.36"), new BigDecimal("1.60"), new BigDecimal("3.20"),
						new BigDecimal("320.00") },
				{ "USD", "CHF", new BigDecimal("0.90"), new BigDecimal("2.00"), new BigDecimal("4.00"),
						new BigDecimal("400.00") },
				{ "USD", "CNY", new BigDecimal("7.27"), new BigDecimal("1.40"), new BigDecimal("2.80"),
						new BigDecimal("280.00") },

				// EUR Cross Pairs
				{ "EUR", "GBP", new BigDecimal("0.86"), new BigDecimal("1.85"), new BigDecimal("3.70"),
						new BigDecimal("370.00") },
				{ "EUR", "JPY", new BigDecimal("162.75"), new BigDecimal("1.35"), new BigDecimal("2.70"),
						new BigDecimal("270.00") },
				{ "EUR", "AUD", new BigDecimal("1.62"), new BigDecimal("1.90"), new BigDecimal("3.80"),
						new BigDecimal("380.00") },
				{ "EUR", "CAD", new BigDecimal("1.48"), new BigDecimal("1.70"), new BigDecimal("3.40"),
						new BigDecimal("340.00") },
				{ "EUR", "CHF", new BigDecimal("0.98"), new BigDecimal("2.10"), new BigDecimal("4.20"),
						new BigDecimal("420.00") },

				// GBP Cross Pairs
				{ "GBP", "JPY", new BigDecimal("189.25"), new BigDecimal("1.45"), new BigDecimal("2.90"),
						new BigDecimal("290.00") },
				{ "GBP", "AUD", new BigDecimal("1.88"), new BigDecimal("2.00"), new BigDecimal("4.00"),
						new BigDecimal("400.00") },
				{ "GBP", "CAD", new BigDecimal("1.72"), new BigDecimal("1.80"), new BigDecimal("3.60"),
						new BigDecimal("360.00") },

				// Emerging Market Currencies
				{ "USD", "SGD", new BigDecimal("1.35"), new BigDecimal("1.50"), new BigDecimal("3.00"),
						new BigDecimal("300.00") },
				{ "USD", "HKD", new BigDecimal("7.82"), new BigDecimal("1.20"), new BigDecimal("2.40"),
						new BigDecimal("240.00") },
				{ "USD", "THB", new BigDecimal("36.25"), new BigDecimal("2.20"), new BigDecimal("4.40"),
						new BigDecimal("440.00") },
				{ "USD", "MYR", new BigDecimal("4.68"), new BigDecimal("2.30"), new BigDecimal("4.60"),
						new BigDecimal("460.00") },
				{ "USD", "IDR", new BigDecimal("15750.00"), new BigDecimal("2.80"), new BigDecimal("5.60"),
						new BigDecimal("560.00") },
				{ "USD", "KRW", new BigDecimal("1325.50"), new BigDecimal("1.80"), new BigDecimal("3.60"),
						new BigDecimal("360.00") },

				// Middle East & Africa
				{ "USD", "AED", new BigDecimal("3.67"), new BigDecimal("1.30"), new BigDecimal("2.60"),
						new BigDecimal("260.00") },
				{ "USD", "SAR", new BigDecimal("3.75"), new BigDecimal("1.40"), new BigDecimal("2.80"),
						new BigDecimal("280.00") },
				{ "USD", "ZAR", new BigDecimal("18.75"), new BigDecimal("3.50"), new BigDecimal("7.00"),
						new BigDecimal("700.00") },

				// Cryptocurrency Pairs (for reference)
				{ "BTC", "USD", new BigDecimal("43500.00"), new BigDecimal("5.00"), new BigDecimal("10.00"),
						new BigDecimal("1000.00") },
				{ "ETH", "USD", new BigDecimal("2650.00"), new BigDecimal("4.50"), new BigDecimal("9.00"),
						new BigDecimal("900.00") } };

		for (Object[] rateData : exchangeRates) {
			CurrencyExchange exchange = new CurrencyExchange();
			exchange.setFromCurrency((String) rateData[0]);
			exchange.setToCurrency((String) rateData[1]);
			exchange.setConversionRate((BigDecimal) rateData[2]);
			exchange.setConversionFeePercent((BigDecimal) rateData[3]);
			exchange.setMinimumFee((BigDecimal) rateData[4]);
			exchange.setMaximumFee((BigDecimal) rateData[5]);
			exchange.setIsActive(true);
			exchange.setRateSource(RateSource.MANUAL);
			exchange.setLastUpdated(LocalDateTime.now());
			exchange.setCreatedAt(LocalDateTime.now());

			currencyExchangeRepository.save(exchange);
		}
		logger.info("✓ {} Currency Exchange Rates created", exchangeRates.length);
	}
}
