package com.tss.aml.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.aml.dto.request.ComplianceOfficerRequest;
import com.tss.aml.dto.request.KeywordRequest;
import com.tss.aml.dto.request.RiskyCountryRequest;
import com.tss.aml.dto.request.RuleRequest;
import com.tss.aml.entity.Admin;
import com.tss.aml.entity.ComplianceOfficer;
import com.tss.aml.entity.RiskyCountry;
import com.tss.aml.entity.Rule;
import com.tss.aml.entity.SuspiciousKeyword;
import com.tss.aml.entity.User;
import com.tss.aml.repository.AdminRepository;
import com.tss.aml.repository.ComplianceOfficerRepository;
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
}