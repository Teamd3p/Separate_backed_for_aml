package com.tss.aml.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.aml.dto.request.AccountUpdateRequest;
import com.tss.aml.dto.request.ComplianceOfficerRequest;
import com.tss.aml.dto.request.CustomerUpdateRequest;
import com.tss.aml.dto.request.KeywordRequest;
import com.tss.aml.dto.request.RiskyCountryRequest;
import com.tss.aml.dto.request.RuleRequest;
import com.tss.aml.entity.Account;
import com.tss.aml.entity.Admin;
import com.tss.aml.entity.ComplianceOfficer;
import com.tss.aml.entity.Customer;
import com.tss.aml.entity.KycDocument;
import com.tss.aml.entity.RiskyCountry;
import com.tss.aml.entity.Rule;
import com.tss.aml.entity.SuspiciousKeyword;
import com.tss.aml.entity.User;
import com.tss.aml.entity.enums.AccountStatus;
import com.tss.aml.entity.enums.AccountType;
import com.tss.aml.entity.enums.KycStatus;
import com.tss.aml.repository.AccountRepository;
import com.tss.aml.repository.AdminRepository;
import com.tss.aml.repository.ComplianceOfficerRepository;
import com.tss.aml.repository.CustomerRepository;
import com.tss.aml.repository.KycDocumentRepository;
import com.tss.aml.repository.RiskyCountryRepository;
import com.tss.aml.repository.RuleRepository;
import com.tss.aml.repository.SuspiciousKeywordRepository;
import com.tss.aml.service.AdminService;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private ComplianceOfficerRepository complianceOfficerRepo;
    
    @Autowired
    private RuleRepository ruleRepo;
    
    @Autowired
    private SuspiciousKeywordRepository keywordRepo;
    
    @Autowired
    private RiskyCountryRepository riskyCountryRepo;
    
    @Autowired
    private AdminRepository adminRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CustomerRepository customerRepo;
    
    @Autowired
    private AccountRepository accountRepo;
    
    @Autowired
    private KycDocumentRepository kycDocumentRepo;

    private Admin getCurrentAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            return adminRepo.findById(user.getUserId()).orElse(null);
        }
        return null;
    }

    // === COMPLIANCE OFFICERS ===
    @Override
    public ComplianceOfficer createComplianceOfficer(ComplianceOfficerRequest request) {
        if (complianceOfficerRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        ComplianceOfficer officer = new ComplianceOfficer(
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getFirstName(),
            request.getLastName(),
            request.getPhone()
        );
        // Set officer as active and verified immediately
        officer.setStatus(com.tss.aml.entity.enums.UserStatus.ACTIVE);
        officer.setEmailVerified(true);
        return complianceOfficerRepo.save(officer);
    }

    @Override
    public List<ComplianceOfficer> getAllComplianceOfficers() {
        return complianceOfficerRepo.findAll();
    }

    @Override
    public void deleteComplianceOfficer(Long id) {
        complianceOfficerRepo.deleteById(id);
    }

    // === RULES ===
    @Override
    public Rule createRule(RuleRequest request) {
        Rule rule = new Rule(
            request.getName(),
            request.getType(),
            request.getConditions(),
            request.getRiskScoreImpact()
        );
        rule.setDescription(request.getDescription());
        rule.setActive(request.getActive());
        
        // Set updatedBy and updatedAt
        Admin currentAdmin = getCurrentAdmin();
        if (currentAdmin != null) {
            rule.setUpdatedBy(currentAdmin);
        }
        rule.setUpdatedAt(LocalDateTime.now());
        
        return ruleRepo.save(rule);
    }

    @Override
    public Rule updateRule(Long id, RuleRequest request) {
        Rule rule = ruleRepo.findById(id).orElseThrow(() -> new RuntimeException("Rule not found"));
        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        rule.setType(request.getType());
        rule.setConditions(request.getConditions());
        rule.setRiskScoreImpact(request.getRiskScoreImpact());
        rule.setActive(request.getActive());
        
        // Set updatedBy and updatedAt
        Admin currentAdmin = getCurrentAdmin();
        if (currentAdmin != null) {
            rule.setUpdatedBy(currentAdmin);
        }
        rule.setUpdatedAt(LocalDateTime.now());
        
        return ruleRepo.save(rule);
    }

    @Override
    public void deleteRule(Long id) {
        ruleRepo.deleteById(id);
    }

    @Override
    public List<Rule> getAllRules() {
        return ruleRepo.findAll();
    }

    // === KEYWORDS ===
    @Override
    public SuspiciousKeyword createKeyword(KeywordRequest request) {
        SuspiciousKeyword keyword = new SuspiciousKeyword(
            request.getWord(),
            request.getCategory(),
            request.getSeverity()
        );
        keyword.setActive(request.getActive());
        return keywordRepo.save(keyword);
    }

    @Override
    public SuspiciousKeyword updateKeyword(Long id, KeywordRequest request) {
        SuspiciousKeyword kw = keywordRepo.findById(id).orElseThrow(() -> new RuntimeException("Keyword not found"));
        kw.setWord(request.getWord());
        kw.setCategory(request.getCategory());
        kw.setSeverity(request.getSeverity());
        kw.setActive(request.getActive());
        return keywordRepo.save(kw);
    }

    @Override
    public void deleteKeyword(Long id) {
        keywordRepo.deleteById(id);
    }

    @Override
    public List<SuspiciousKeyword> getAllKeywords() {
        return keywordRepo.findAll();
    }

    // === RISKY COUNTRIES ===
    @Override
    public RiskyCountry createRiskyCountry(RiskyCountryRequest request) {
        // Check if country already exists
        if (riskyCountryRepo.findById(request.getCountryCode().toUpperCase()).isPresent()) {
            throw new RuntimeException("Country with code " + request.getCountryCode() + " already exists");
        }
        
        RiskyCountry country = new RiskyCountry(
            request.getCountryCode().toUpperCase(),
            request.getCountryName(),
            request.getRiskLevel()
        );
        
        // Set lastUpdatedBy
        Admin currentAdmin = getCurrentAdmin();
        if (currentAdmin != null) {
            country.setLastUpdatedBy(currentAdmin);
        }
        country.setLastUpdatedAt(LocalDateTime.now());
        
        return riskyCountryRepo.save(country);
    }

    @Override
    public RiskyCountry updateRiskyCountry(String countryCode, RiskyCountryRequest request) {
        RiskyCountry country = riskyCountryRepo.findById(countryCode.toUpperCase())
            .orElseThrow(() -> new RuntimeException("Country not found"));
        country.setCountryName(request.getCountryName());
        country.setRiskLevel(request.getRiskLevel());
        
        // Set lastUpdatedBy
        Admin currentAdmin = getCurrentAdmin();
        if (currentAdmin != null) {
            country.setLastUpdatedBy(currentAdmin);
        }
        country.setLastUpdatedAt(LocalDateTime.now());
        
        return riskyCountryRepo.save(country);
    }

    @Override
    public void deleteRiskyCountry(String countryCode) {
        if (!riskyCountryRepo.existsById(countryCode.toUpperCase())) {
            throw new RuntimeException("Country not found");
        }
        riskyCountryRepo.deleteById(countryCode.toUpperCase());
    }

    @Override
    public List<RiskyCountry> getAllRiskyCountries() {
        return riskyCountryRepo.findAll();
    }
    
    // === CUSTOMER MANAGEMENT ===
    @Override
    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }
    
    @Override
    public Customer getCustomerById(Long id) {
        return customerRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
    
    @Override
    public Customer updateCustomer(Long id, CustomerUpdateRequest request) {
        Customer customer = getCustomerById(id);
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setContactNumber(request.getPhoneNumber());
        customer.setStreet(request.getAddress());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setNationality(request.getCountryCode());
        customer.setPincode(request.getPostalCode());
        // Note: riskProfile field doesn't exist in Customer entity - removing this line
        // customer.setRiskProfile(request.getRiskProfile());
        return customerRepo.save(customer);
    }
    
    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepo.existsById(id)) {
            throw new RuntimeException("Customer not found");
        }
        customerRepo.deleteById(id);
    }
    
    // === ACCOUNT MANAGEMENT ===
    @Override
    public List<Account> getAllAccounts() {
        return accountRepo.findAll();
    }
    
    @Override
    public List<Account> getAccountsByCustomerId(Long customerId) {
        return accountRepo.findByCustomerUserId(customerId);
    }
    
    @Override
    public Account updateAccount(Long accountId, AccountUpdateRequest request) {
        Account account = accountRepo.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        // Convert string to AccountType enum
        if (request.getAccountType() != null) {
            account.setAccountType(AccountType.valueOf(request.getAccountType().toUpperCase()));
        }
        
        if (request.getBalance() != null) {
            account.setBalance(request.getBalance());
        }
        
        // Note: dailyTransactionLimit, monthlyTransactionLimit, and riskLevel fields don't exist in Account entity
        // These would need to be added to the Account entity if required
        
        // Convert string to AccountStatus enum
        if (request.getStatus() != null) {
            account.setStatus(AccountStatus.valueOf(request.getStatus().toUpperCase()));
        }
        
        account.setUpdatedAt(java.time.LocalDateTime.now());
        return accountRepo.save(account);
    }
    
    @Override
    public void freezeAccount(Long accountId) {
        Account account = accountRepo.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setStatus(AccountStatus.FROZEN);
        account.setUpdatedAt(java.time.LocalDateTime.now());
        accountRepo.save(account);
    }
    
    @Override
    public void unfreezeAccount(Long accountId) {
        Account account = accountRepo.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setStatus(AccountStatus.ACTIVE);
        account.setUpdatedAt(java.time.LocalDateTime.now());
        accountRepo.save(account);
    }
    
    // === KYC DOCUMENT VERIFICATION MANAGEMENT ===
    @Override
    public List<KycDocument> getPendingDocuments() {
        return kycDocumentRepo.findByStatus(KycStatus.PENDING);
    }
    
    @Override
    public List<KycDocument> getDocumentsRequiringManualReview() {
        return kycDocumentRepo.findByRequiresManualReviewTrue();
    }
    
    @Override
    public KycDocument verifyDocument(Long documentId, Long officerId, String notes, boolean approved) {
        KycDocument document = kycDocumentRepo.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found"));
        
        ComplianceOfficer officer = complianceOfficerRepo.findById(officerId)
            .orElseThrow(() -> new RuntimeException("Compliance officer not found"));
        
        document.setVerifiedBy(officer);
        document.setVerificationNotes(notes);
        document.setVerificationTimestamp(LocalDateTime.now());
        document.setStatus(approved ? KycStatus.VERIFIED : KycStatus.REJECTED);
        document.setValidated(approved);
        document.setRequiresManualReview(false);
        
        return kycDocumentRepo.save(document);
    }
    
    @Override
    public KycDocument rejectDocument(Long documentId, Long officerId, String rejectionReason) {
        return verifyDocument(documentId, officerId, rejectionReason, false);
    }
}