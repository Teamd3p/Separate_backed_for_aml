package com.tss.aml.service;

import java.util.List;

import com.tss.aml.dto.request.ComplianceOfficerRequest;
import com.tss.aml.dto.request.KeywordRequest;
import com.tss.aml.dto.request.RiskyCountryRequest;
import com.tss.aml.dto.request.RuleRequest;
import com.tss.aml.entity.ComplianceOfficer;
import com.tss.aml.entity.RiskyCountry;
import com.tss.aml.entity.Rule;
import com.tss.aml.entity.SuspiciousKeyword;

public interface AdminService {
    // Compliance Officers
    ComplianceOfficer createComplianceOfficer(ComplianceOfficerRequest request);
    List<ComplianceOfficer> getAllComplianceOfficers();
    void deleteComplianceOfficer(Long id);

    // Rules
    Rule createRule(RuleRequest request);
    Rule updateRule(Long id, RuleRequest request);
    void deleteRule(Long id);
    List<Rule> getAllRules();

    // Keywords
    SuspiciousKeyword createKeyword(KeywordRequest request);
    SuspiciousKeyword updateKeyword(Long id, KeywordRequest request);
    void deleteKeyword(Long id);
    List<SuspiciousKeyword> getAllKeywords();

    // Risky Countries
    RiskyCountry createRiskyCountry(RiskyCountryRequest request);
    RiskyCountry updateRiskyCountry(String countryCode, RiskyCountryRequest request);
    void deleteRiskyCountry(String countryCode);
    List<RiskyCountry> getAllRiskyCountries();
}