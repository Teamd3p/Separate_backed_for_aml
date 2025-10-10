package com.tss.aml.service;

//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.tss.aml.entity.*;
//import com.tss.aml.entity.enums.*;
//import com.tss.aml.repository.*;
//
//@Service
public class AmlDataInitializationService {
//    
//    private static final Logger logger = LoggerFactory.getLogger(AmlDataInitializationService.class);
//    
//    @Autowired private AdminRepository adminRepository;
//    @Autowired private ComplianceOfficerRepository complianceOfficerRepository;
//    @Autowired private CustomerRepository customerRepository;
//    @Autowired private AccountRepository accountRepository;
//    @Autowired private KycDocumentRepository kycDocumentRepository;
//    @Autowired private RuleRepository ruleRepository;
//    @Autowired private RiskyCountryRepository riskyCountryRepository;
//    @Autowired private SuspiciousKeywordRepository suspiciousKeywordRepository;
//    @Autowired private CurrencyExchangeRepository currencyExchangeRepository;
//    @Autowired private PasswordEncoder passwordEncoder;
//    
//    @Override
//    @Transactional
//    public void run(String... args) throws Exception {
//        logger.info("🚀 Starting AML Data Initialization...");
//        
//        if (adminRepository.count() > 0) {
//            logger.info("✅ Data already exists, skipping initialization");
//            return;
//        }
//        
//        initializeAdmins();
//        initializeComplianceOfficers();
//        initializeCustomers();
//        initializeAccounts();
//        initializeKycDocuments();
//        initializeRules();
//        initializeRiskyCountries();
//        initializeSuspiciousKeywords();
//        initializeCurrencyExchangeRates();
//        
//        logger.info("✅ AML Data Initialization completed successfully!");
//    }
//    
//    private void initializeAdmins() {
//        logger.info("👤 Initializing Admins...");
//        
//        List<Admin> admins = Arrays.asList(
//            createAdmin("admin@aml.com", "Admin123!", "System", "Administrator"),
//            createAdmin("john.admin@aml.com", "Admin123!", "John", "Smith"),
//            createAdmin("sarah.admin@aml.com", "Admin123!", "Sarah", "Johnson")
//        );
//        
//        adminRepository.saveAll(admins);
//        logger.info("✅ Created {} admins", admins.size());
//    }
//    
//    private void initializeComplianceOfficers() {
//        logger.info("👮 Initializing Compliance Officers...");
//        
//        List<ComplianceOfficer> officers = Arrays.asList(
//            createComplianceOfficer("officer1@aml.com", "Officer123!", "Michael", "Brown", "CO001"),
//            createComplianceOfficer("officer2@aml.com", "Officer123!", "Emily", "Davis", "CO002"),
//            createComplianceOfficer("officer3@aml.com", "Officer123!", "David", "Wilson", "CO003")
//        );
//        
//        complianceOfficerRepository.saveAll(officers);
//        logger.info("✅ Created {} compliance officers", officers.size());
//    }
//    
//    private void initializeCustomers() {
//        logger.info("👥 Initializing Customers...");
//        
//        List<Customer> customers = Arrays.asList(
//            createCustomer("alice@example.com", "Customer123!", "Alice", "Johnson", 
//                LocalDate.of(1990, 5, 15), "Indian", "9876543210", KycStatus.VERIFIED),
//            createCustomer("bob@example.com", "Customer123!", "Bob", "Smith", 
//                LocalDate.of(1985, 8, 22), "American", "9876543211", KycStatus.PENDING),
//            createCustomer("charlie@example.com", "Customer123!", "Charlie", "Brown", 
//                LocalDate.of(1992, 12, 3), "British", "9876543212", KycStatus.REJECTED),
//            createCustomer("diana@example.com", "Customer123!", "Diana", "Wilson", 
//                LocalDate.of(1988, 3, 18), "Canadian", "9876543213", KycStatus.VERIFIED),
//            createCustomer("eve@example.com", "Customer123!", "Eve", "Davis", 
//                LocalDate.of(1995, 7, 9), "Australian", "9876543214", KycStatus.PENDING)
//        );
//        
//        customerRepository.saveAll(customers);
//        logger.info("✅ Created {} customers", customers.size());
//    }
//    
//    private void initializeAccounts() {
//        logger.info("🏦 Initializing Accounts...");
//        
//        List<Customer> customers = customerRepository.findAll();
//        List<Account> accounts = Arrays.asList(
//            createAccount(customers.get(0), "ACC001001", AccountType.SAVING, new BigDecimal("50000.00"), AccountStatus.ACTIVE),
//            createAccount(customers.get(0), "ACC001002", AccountType.CURRENT, new BigDecimal("100000.00"), AccountStatus.ACTIVE),
//            createAccount(customers.get(1), "ACC002001", AccountType.SAVING, new BigDecimal("25000.00"), AccountStatus.ACTIVE),
//            createAccount(customers.get(2), "ACC003001", AccountType.CURRENT, new BigDecimal("75000.00"), AccountStatus.FROZEN),
//            createAccount(customers.get(3), "ACC004001", AccountType.SALARY, new BigDecimal("80000.00"), AccountStatus.ACTIVE),
//            createAccount(customers.get(4), "ACC005001", AccountType.SAVING, new BigDecimal("30000.00"), AccountStatus.PENDING)
//        );
//        
//        accountRepository.saveAll(accounts);
//        logger.info("✅ Created {} accounts", accounts.size());
//    }
//    
//    private void initializeKycDocuments() {
//        logger.info("📄 Initializing KYC Documents...");
//        
//        List<Customer> customers = customerRepository.findAll();
//        List<KycDocument> documents = Arrays.asList(
//            createKycDocument(customers.get(0), DocumentType.PAN, "alice_pan.pdf", KycStatus.VERIFIED, 15),
//            createKycDocument(customers.get(0), DocumentType.AADHAAR, "alice_aadhaar.pdf", KycStatus.VERIFIED, 20),
//            createKycDocument(customers.get(1), DocumentType.PASSPORT, "bob_passport.pdf", KycStatus.PENDING, 45),
//            createKycDocument(customers.get(2), DocumentType.DRIVING_LICENSE, "charlie_dl.pdf", KycStatus.REJECTED, 85),
//            createKycDocument(customers.get(3), DocumentType.VOTER_ID, "diana_voter.pdf", KycStatus.VERIFIED, 25),
//            createKycDocument(customers.get(4), DocumentType.PAN, "eve_pan.pdf", KycStatus.PENDING, 60)
//        );
//        
//        kycDocumentRepository.saveAll(documents);
//        logger.info("✅ Created {} KYC documents", documents.size());
//    }
//    
//    private void initializeRules() {
//        logger.info("📋 Initializing AML Rules...");
//        
//        List<Rule> rules = Arrays.asList(
//            createRule("Critical Risk Country Block", "Block or flag transactions to/from countries on the critical risk list.", 
//                RuleType.GEOGRAPHIC, "{\"riskType\": \"CRITICAL\", \"listName\": \"critical_country_list\"}", 95),
//            createRule("Suspicious Language Detection (NLP)", "Flags transactions with high-risk keywords in notes using the NLP service.", 
//                RuleType.NLP_CONTEXT, "{\"nlpModel\": \"keyword_detection_v1\", \"listName\": \"suspicious_keywords_main\"}", 70),
//            createRule("High-Value Transfer (Account-Type Specific)", "Applies dynamic, account-type-specific thresholds for large transactions.", 
//                RuleType.THRESHOLD, "{\"baselineCurrency\": \"INR\", \"accountTypeRules\": {\"CURRENT\": {\"dailyLimitCount\": 1, \"amountThreshold\": 1000000}}}", 65),
//            createRule("Structuring/Smurfing Detection", "Detects smurfing via multiple small deposits (structuring).", 
//                RuleType.PATTERN, "{\"structuringPattern\": {\"timeWindowMinutes\": 1440, \"transactionType\": \"CREDIT\"}}", 90),
//            createRule("Rapid Withdrawals", "Flags unusually fast or large withdrawals based on account-specific thresholds.", 
//                RuleType.FREQUENCY, "{\"referenceRule\": \"High-Value Transfer\", \"transactionTypes\": [\"DEBIT\", \"ATM_WITHDRAWAL\"]}", 75),
//            createRule("Rapid Deposit-Transfer Pattern", "Detects pass-through activity where a deposit is quickly transferred out.", 
//                RuleType.VELOCITY, "{\"timeWindowMinutes\": 60, \"minAmountUSD\": 5000, \"amountMatchPercentage\": 90}", 80),
//            createRule("High Transfer Velocity", "Monitors for unusually high number of significant transfers.", 
//                RuleType.FREQUENCY, "{\"maxTransactions\": 5, \"timeWindowMinutes\": 120, \"minAmountUSD\": 2000}", 80),
//            createRule("Dormant Account Activation", "Flags any activity on a long-dormant account.", 
//                RuleType.BEHAVIOR, "{\"dormantDays\": 180, \"minAmountUSD\": 1, \"checkFlaggedHistory\": true}", 90)
//        );
//        
//        ruleRepository.saveAll(rules);
//        logger.info("✅ Created {} AML rules", rules.size());
//    }
//    
//    private void initializeRiskyCountries() {
//        logger.info("🌍 Initializing Risky Countries...");
//        
//        List<RiskyCountry> countries = Arrays.asList(
//            createRiskyCountry("AF", "Afghanistan", RiskLevel.CRITICAL, "High terrorism risk, sanctions"),
//            createRiskyCountry("IR", "Iran", RiskLevel.CRITICAL, "International sanctions"),
//            createRiskyCountry("KP", "North Korea", RiskLevel.CRITICAL, "International sanctions"),
//            createRiskyCountry("SY", "Syria", RiskLevel.CRITICAL, "Conflict zone, sanctions"),
//            createRiskyCountry("PK", "Pakistan", RiskLevel.HIGH, "High money laundering risk"),
//            createRiskyCountry("BD", "Bangladesh", RiskLevel.HIGH, "High money laundering risk"),
//            createRiskyCountry("MM", "Myanmar", RiskLevel.HIGH, "Political instability"),
//            createRiskyCountry("VE", "Venezuela", RiskLevel.HIGH, "Economic sanctions"),
//            createRiskyCountry("RU", "Russia", RiskLevel.MEDIUM, "Geopolitical tensions"),
//            createRiskyCountry("CN", "China", RiskLevel.MEDIUM, "Enhanced due diligence required")
//        );
//        
//        riskyCountryRepository.saveAll(countries);
//        logger.info("✅ Created {} risky countries", countries.size());
//    }
//    
//    private void initializeSuspiciousKeywords() {
//        logger.info("🔍 Initializing Suspicious Keywords...");
//        
//        List<SuspiciousKeyword> keywords = Arrays.asList(
//            createKeyword("hawala", KeywordCategory.MONEY_LAUNDERING, RiskLevel.HIGH, "Informal money transfer system"),
//            createKeyword("cash pickup", KeywordCategory.MONEY_LAUNDERING, RiskLevel.MEDIUM, "Cash collection service"),
//            createKeyword("invoice manipulation", KeywordCategory.TRADE_BASED, RiskLevel.HIGH, "Trade finance fraud"),
//            createKeyword("over invoicing", KeywordCategory.TRADE_BASED, RiskLevel.HIGH, "Trade finance fraud"),
//            createKeyword("under invoicing", KeywordCategory.TRADE_BASED, RiskLevel.HIGH, "Trade finance fraud"),
//            createKeyword("shell company", KeywordCategory.CORPORATE, RiskLevel.HIGH, "Fictitious business entity"),
//            createKeyword("nominee director", KeywordCategory.CORPORATE, RiskLevel.MEDIUM, "Beneficial ownership concealment"),
//            createKeyword("bearer shares", KeywordCategory.CORPORATE, RiskLevel.HIGH, "Anonymous ownership"),
//            createKeyword("smurfing", KeywordCategory.STRUCTURING, RiskLevel.HIGH, "Breaking large transactions"),
//            createKeyword("layering", KeywordCategory.STRUCTURING, RiskLevel.HIGH, "Complex transaction chains"),
//            createKeyword("placement", KeywordCategory.STRUCTURING, RiskLevel.MEDIUM, "Initial money entry"),
//            createKeyword("integration", KeywordCategory.STRUCTURING, RiskLevel.MEDIUM, "Clean money extraction"),
//            createKeyword("terrorist financing", KeywordCategory.TERRORISM, RiskLevel.CRITICAL, "Terrorism funding"),
//            createKeyword("charity fraud", KeywordCategory.TERRORISM, RiskLevel.HIGH, "Fake charitable organizations"),
//            createKeyword("sanctions evasion", KeywordCategory.SANCTIONS, RiskLevel.CRITICAL, "Circumventing sanctions"),
//            createKeyword("embargo", KeywordCategory.SANCTIONS, RiskLevel.HIGH, "Trade restrictions")
//        );
//        
//        suspiciousKeywordRepository.saveAll(keywords);
//        logger.info("✅ Created {} suspicious keywords", keywords.size());
//    }
//    
//    private void initializeCurrencyExchangeRates() {
//        logger.info("💱 Initializing Currency Exchange Rates...");
//        
//        if (currencyExchangeRepository.count() > 0) {
//            logger.info("✅ Currency rates already exist, skipping");
//            return;
//        }
//        
//        List<CurrencyExchange> rates = Arrays.asList(
//            createExchangeRate("USD", "INR", new BigDecimal("83.25"), new BigDecimal("2.50")),
//            createExchangeRate("USD", "EUR", new BigDecimal("0.92"), new BigDecimal("1.50")),
//            createExchangeRate("USD", "GBP", new BigDecimal("0.79"), new BigDecimal("1.75")),
//            createExchangeRate("EUR", "INR", new BigDecimal("90.45"), new BigDecimal("2.75")),
//            createExchangeRate("GBP", "INR", new BigDecimal("105.30"), new BigDecimal("3.00"))
//        );
//        
//        currencyExchangeRepository.saveAll(rates);
//        logger.info("✅ Created {} currency exchange rates", rates.size());
//    }
//    
//    // Helper methods
//    private Admin createAdmin(String email, String password, String firstName, String lastName) {
//        Admin admin = new Admin();
//        admin.setEmail(email);
//        admin.setPasswordHash(passwordEncoder.encode(password));
//        admin.setRole(Role.ADMIN);
//        admin.setStatus(UserStatus.ACTIVE);
//        admin.setEmailVerified(true);
//        admin.setFirstName(firstName);
//        admin.setLastName(lastName);
//        admin.setEmployeeId("EMP" + System.currentTimeMillis() % 10000);
//        admin.setDepartment("Administration");
//        return admin;
//    }
//    
//    private ComplianceOfficer createComplianceOfficer(String email, String password, String firstName, String lastName, String employeeId) {
//        ComplianceOfficer officer = new ComplianceOfficer();
//        officer.setEmail(email);
//        officer.setPasswordHash(passwordEncoder.encode(password));
//        officer.setRole(Role.COMPLIANCE_OFFICER);
//        officer.setStatus(UserStatus.ACTIVE);
//        officer.setEmailVerified(true);
//        officer.setFirstName(firstName);
//        officer.setLastName(lastName);
//        officer.setEmployeeId(employeeId);
//        officer.setDepartment("Compliance");
//        officer.setCertifications("CAMS, CFE");
//        return officer;
//    }
//    
//    private Customer createCustomer(String email, String password, String firstName, String lastName, 
//                                  LocalDate dob, String nationality, String contactNumber, KycStatus kycStatus) {
//        Customer customer = new Customer();
//        customer.setEmail(email);
//        customer.setPasswordHash(passwordEncoder.encode(password));
//        customer.setRole(Role.CUSTOMER);
//        customer.setStatus(UserStatus.ACTIVE);
//        customer.setEmailVerified(true);
//        customer.setFirstName(firstName);
//        customer.setLastName(lastName);
//        customer.setDateOfBirth(dob);
//        customer.setNationality(nationality);
//        customer.setContactNumber(contactNumber);
//        customer.setKycStatus(kycStatus);
//        customer.setStreet("123 Main St");
//        customer.setCity("Mumbai");
//        customer.setState("Maharashtra");
//        customer.setNation("India");
//        customer.setPincode("400001");
//        return customer;
//    }
//    
//    private Account createAccount(Customer customer, String accountNumber, AccountType type, BigDecimal balance, AccountStatus status) {
//        Account account = new Account();
//        account.setCustomer(customer);
//        account.setAccountNumber(accountNumber);
//        account.setAccountType(type);
//        account.setBalance(balance);
//        account.setStatus(status);
//        account.setCurrency("INR");
//        account.setCreatedAt(LocalDateTime.now());
//        return account;
//    }
//    
//    private KycDocument createKycDocument(Customer customer, DocumentType docType, String fileName, KycStatus status, Integer riskScore) {
//        KycDocument doc = new KycDocument();
//        doc.setCustomer(customer);
//        doc.setDocType(docType);
//        doc.setFileName(fileName);
//        doc.setFileUrl("/uploads/kyc-documents/" + fileName);
//        doc.setStatus(status);
//        doc.setRiskScore(riskScore);
//        doc.setIsValidated(status == KycStatus.VERIFIED);
//        doc.setUploadTimestamp(LocalDateTime.now());
//        return doc;
//    }
//    
//    private Rule createRule(String name, String description, RuleType type, String conditions, Integer riskScore) {
//        Rule rule = new Rule();
//        rule.setName(name);
//        rule.setDescription(description);
//        rule.setType(type);
//        rule.setConditions(conditions);
//        rule.setRiskScoreImpact(riskScore);
//        rule.setIsActive(true);
//        rule.setCreatedAt(LocalDateTime.now());
//        return rule;
//    }
//    
//    private RiskyCountry createRiskyCountry(String countryCode, String countryName, RiskLevel riskLevel, String reason) {
//        RiskyCountry country = new RiskyCountry();
//        country.setCountryCode(countryCode);
//        country.setCountryName(countryName);
//        country.setRiskLevel(riskLevel);
//        country.setReason(reason);
//        country.setIsActive(true);
//        country.setCreatedAt(LocalDateTime.now());
//        return country;
//    }
//    
//    private SuspiciousKeyword createKeyword(String keyword, KeywordCategory category, RiskLevel riskLevel, String description) {
//        SuspiciousKeyword suspiciousKeyword = new SuspiciousKeyword();
//        suspiciousKeyword.setKeyword(keyword);
//        suspiciousKeyword.setCategory(category);
//        suspiciousKeyword.setRiskLevel(riskLevel);
//        suspiciousKeyword.setDescription(description);
//        suspiciousKeyword.setIsActive(true);
//        suspiciousKeyword.setCreatedAt(LocalDateTime.now());
//        return suspiciousKeyword;
//    }
//    
//    private CurrencyExchange createExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate, BigDecimal feePercent) {
//        CurrencyExchange exchange = new CurrencyExchange();
//        exchange.setFromCurrency(fromCurrency);
//        exchange.setToCurrency(toCurrency);
//        exchange.setConversionRate(rate);
//        exchange.setConversionFeePercent(feePercent);
//        exchange.setMinimumFee(new BigDecimal("5.00"));
//        exchange.setMaximumFee(new BigDecimal("500.00"));
//        exchange.setIsActive(true);
//        exchange.setRateSource(RateSource.MANUAL);
//        exchange.setLastUpdated(LocalDateTime.now());
//        exchange.setCreatedAt(LocalDateTime.now());
//        return exchange;
//    }
}
