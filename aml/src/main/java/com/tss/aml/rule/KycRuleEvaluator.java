package com.tss.aml.rule;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tss.aml.entity.Alert;
import com.tss.aml.entity.Customer;
import com.tss.aml.entity.KycDocument;
import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.enums.AlertStatus;
import com.tss.aml.entity.enums.DocumentType;
import com.tss.aml.entity.enums.KycStatus;
import com.tss.aml.repository.AlertRepository;
import com.tss.aml.repository.KycDocumentRepository;

@Service
public class KycRuleEvaluator  {

    @Autowired
    private KycDocumentRepository kycDocumentRepository;
    
    @Autowired
    private AlertRepository alertRepository;

    public void evaluateKycRulesForTransaction(Transaction transaction) {
        Customer customer = null;
   
        
        if (customer == null) {
            return; // External transaction, no KYC check needed
        }
        
        List<KycDocument> customerDocuments = kycDocumentRepository.findByCustomerUserId(customer.getUserId());
        
        // Rule 1: Check if customer has complete KYC
        evaluateKycCompletenessRule(transaction, customer, customerDocuments);
        
        // Rule 2: Check for expired documents
        evaluateExpiredDocumentsRule(transaction, customer, customerDocuments);
        
        // Rule 3: Check for high-risk documents
        evaluateHighRiskDocumentsRule(transaction, customer, customerDocuments);
        
        // Rule 4: Check for pending verifications
        evaluatePendingVerificationRule(transaction, customer, customerDocuments);
        
        // Rule 5: Check for rejected documents
        evaluateRejectedDocumentsRule(transaction, customer, customerDocuments);
    }

    private void evaluateKycCompletenessRule(Transaction transaction, Customer customer, List<KycDocument> documents) {
        long verifiedDocuments = documents.stream()
            .filter(doc -> doc.getStatus() == KycStatus.VERIFIED)
            .count();
        
        // Require at least 2 verified documents for complete KYC
        if (verifiedDocuments < 2) {
            String ruleDescription = "KYC Incomplete - Customer has only " + verifiedDocuments + " verified documents (minimum 2 required)";
            int riskScore = calculateKycIncompletenessRiskScore(verifiedDocuments);
            
            createKycAlert(transaction, customer, "KYC_INCOMPLETE", ruleDescription, riskScore);
        }
    }

    private void evaluateExpiredDocumentsRule(Transaction transaction, Customer customer, List<KycDocument> documents) {
        List<KycDocument> expiredDocs = documents.stream()
            .filter(doc -> doc.getStatus() == KycStatus.VERIFIED)
            .toList();
        
        if (!expiredDocs.isEmpty()) {
            String ruleDescription = "Expired KYC Documents - " + expiredDocs.size() + " verified documents have expired";
            int riskScore = Math.min(80, 40 + (expiredDocs.size() * 20)); // Higher risk for more expired docs
            
            createKycAlert(transaction, customer, "KYC_EXPIRED", ruleDescription, riskScore);
        }
    }

    private void evaluateHighRiskDocumentsRule(Transaction transaction, Customer customer, List<KycDocument> documents) {
        List<KycDocument> highRiskDocs = documents.stream()
            .filter(doc -> doc.getRiskScore() != null && doc.getRiskScore() >= 70)
            .toList();
        
        if (!highRiskDocs.isEmpty()) {
            String ruleDescription = "High Risk KYC Documents - " + highRiskDocs.size() + " documents flagged as high risk";
            int riskScore = Math.min(90, 60 + (highRiskDocs.size() * 15));
            
            createKycAlert(transaction, customer, "KYC_HIGH_RISK", ruleDescription, riskScore);
        }
    }

    private void evaluatePendingVerificationRule(Transaction transaction, Customer customer, List<KycDocument> documents) {
        List<KycDocument> pendingDocs = documents.stream()
            .filter(doc -> doc.getStatus() == KycStatus.PENDING)
            .toList();
        
        // Check for old pending documents (more than 7 days)
        List<KycDocument> oldPendingDocs = pendingDocs.stream()
            .filter(doc -> ChronoUnit.DAYS.between(doc.getUploadTimestamp(), LocalDateTime.now()) > 7)
            .toList();
        
        if (!oldPendingDocs.isEmpty()) {
            String ruleDescription = "Pending KYC Verification - " + oldPendingDocs.size() + " documents pending verification for over 7 days";
            int riskScore = Math.min(70, 30 + (oldPendingDocs.size() * 20));
            
            createKycAlert(transaction, customer, "KYC_PENDING_VERIFICATION", ruleDescription, riskScore);
        }
    }

    private void evaluateRejectedDocumentsRule(Transaction transaction, Customer customer, List<KycDocument> documents) {
        List<KycDocument> rejectedDocs = documents.stream()
            .filter(doc -> doc.getStatus() == KycStatus.REJECTED)
            .toList();
        
        if (!rejectedDocs.isEmpty()) {
            String ruleDescription = "Rejected KYC Documents - " + rejectedDocs.size() + " documents have been rejected";
            int riskScore = Math.min(85, 50 + (rejectedDocs.size() * 25));
            
            createKycAlert(transaction, customer, "KYC_REJECTED", ruleDescription, riskScore);
        }
    }

    private int calculateKycIncompletenessRiskScore(long verifiedDocuments) {
        if (verifiedDocuments == 0) {
            return 90; // Very high risk - no verified documents
        } else if (verifiedDocuments == 1) {
            return 60; // Medium-high risk - only one verified document
        }
        return 30; // Lower risk - has some documents but not complete
    }

    private void createKycAlert(Transaction transaction, Customer customer, String ruleType, String ruleDescription, int riskScore) {
        Alert alert = new Alert();
        alert.setTransaction(transaction);
        alert.setCustomer(customer);
        alert.setRuleTriggered(ruleType + ": " + ruleDescription);
        alert.setRiskScore(riskScore);
        alert.setStatus(AlertStatus.OPEN);
        alert.setCreatedAt(LocalDateTime.now());
        
        alertRepository.save(alert);
    }

    public KycComplianceReport generateKycComplianceReport(Long customerId) {
        List<KycDocument> documents = kycDocumentRepository.findByCustomerUserId(customerId);
        
        KycComplianceReport report = new KycComplianceReport();
        report.setCustomerId(customerId);
        report.setTotalDocuments(documents.size());
        
        // Count documents by status
        long verified = documents.stream().filter(d -> d.getStatus() == KycStatus.VERIFIED).count();
        long pending = documents.stream().filter(d -> d.getStatus() == KycStatus.PENDING).count();
        long rejected = documents.stream().filter(d -> d.getStatus() == KycStatus.REJECTED).count();
        long expired = documents.stream().filter(d -> d.getStatus() == KycStatus.EXPIRED).count();
        
        report.setVerifiedDocuments((int) verified);
        report.setPendingDocuments((int) pending);
        report.setRejectedDocuments((int) rejected);
        report.setExpiredDocuments((int) expired);
        
        // Check document type coverage
        List<DocumentType> requiredTypes = List.of(DocumentType.PAN, DocumentType.AADHAAR);
        List<DocumentType> verifiedTypes = documents.stream()
            .filter(d -> d.getStatus() == KycStatus.VERIFIED)
            .map(KycDocument::getDocType)
            .distinct()
            .toList();
        
        report.setRequiredDocumentTypes(requiredTypes);
        report.setVerifiedDocumentTypes(verifiedTypes);
        report.setKycComplete(verifiedTypes.containsAll(requiredTypes) && verified >= 2);
        
        // Calculate overall compliance score
        int complianceScore = calculateComplianceScore(report);
        report.setComplianceScore(complianceScore);
        
        // Identify compliance issues
        List<String> issues = identifyComplianceIssues(documents, report);
        report.setComplianceIssues(issues);
        
        return report;
    }

    private int calculateComplianceScore(KycComplianceReport report) {
        int score = 0;
        
        // Base score for having documents
        if (report.getTotalDocuments() > 0) {
            score += 20;
        }
        
        // Score for verified documents
        score += Math.min(40, report.getVerifiedDocuments() * 20);
        
        // Bonus for complete KYC
        if (report.isKycComplete()) {
            score += 30;
        }
        
        // Penalty for rejected documents
        score -= report.getRejectedDocuments() * 10;
        
        // Penalty for expired documents
        score -= report.getExpiredDocuments() * 15;
        
        return Math.max(0, Math.min(100, score));
    }

    private List<String> identifyComplianceIssues(List<KycDocument> documents, KycComplianceReport report) {
        List<String> issues = new ArrayList<>();
        
        if (!report.isKycComplete()) {
            issues.add("KYC is incomplete - missing required document types or insufficient verified documents");
        }
        
        if (report.getRejectedDocuments() > 0) {
            issues.add("Has " + report.getRejectedDocuments() + " rejected documents");
        }
        
        if (report.getExpiredDocuments() > 0) {
            issues.add("Has " + report.getExpiredDocuments() + " expired documents");
        }
        
        if (report.getPendingDocuments() > 0) {
            long oldPending = documents.stream()
                .filter(d -> d.getStatus() == KycStatus.PENDING)
                .filter(d -> ChronoUnit.DAYS.between(d.getUploadTimestamp(), LocalDateTime.now()) > 7)
                .count();
            
            if (oldPending > 0) {
                issues.add("Has " + oldPending + " documents pending verification for over 7 days");
            }
        }
        
        long highRiskDocs = documents.stream()
            .filter(d -> d.getRiskScore() != null && d.getRiskScore() >= 70)
            .count();
        
        if (highRiskDocs > 0) {
            issues.add("Has " + highRiskDocs + " high-risk documents");
        }
        
        return issues;
    }

    // Inner class for KYC compliance reporting
    public static class KycComplianceReport {
        private Long customerId;
        private int totalDocuments;
        private int verifiedDocuments;
        private int pendingDocuments;
        private int rejectedDocuments;
        private int expiredDocuments;
        private List<DocumentType> requiredDocumentTypes;
        private List<DocumentType> verifiedDocumentTypes;
        private boolean kycComplete;
        private int complianceScore;
        private List<String> complianceIssues;
        
        // Getters and setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        
        public int getTotalDocuments() { return totalDocuments; }
        public void setTotalDocuments(int totalDocuments) { this.totalDocuments = totalDocuments; }
        
        public int getVerifiedDocuments() { return verifiedDocuments; }
        public void setVerifiedDocuments(int verifiedDocuments) { this.verifiedDocuments = verifiedDocuments; }
        
        public int getPendingDocuments() { return pendingDocuments; }
        public void setPendingDocuments(int pendingDocuments) { this.pendingDocuments = pendingDocuments; }
        
        public int getRejectedDocuments() { return rejectedDocuments; }
        public void setRejectedDocuments(int rejectedDocuments) { this.rejectedDocuments = rejectedDocuments; }
        
        public int getExpiredDocuments() { return expiredDocuments; }
        public void setExpiredDocuments(int expiredDocuments) { this.expiredDocuments = expiredDocuments; }
        
        public List<DocumentType> getRequiredDocumentTypes() { return requiredDocumentTypes; }
        public void setRequiredDocumentTypes(List<DocumentType> requiredDocumentTypes) { this.requiredDocumentTypes = requiredDocumentTypes; }
        
        public List<DocumentType> getVerifiedDocumentTypes() { return verifiedDocumentTypes; }
        public void setVerifiedDocumentTypes(List<DocumentType> verifiedDocumentTypes) { this.verifiedDocumentTypes = verifiedDocumentTypes; }
        
        public boolean isKycComplete() { return kycComplete; }
        public void setKycComplete(boolean kycComplete) { this.kycComplete = kycComplete; }
        
        public int getComplianceScore() { return complianceScore; }
        public void setComplianceScore(int complianceScore) { this.complianceScore = complianceScore; }
        
        public List<String> getComplianceIssues() { return complianceIssues; }
        public void setComplianceIssues(List<String> complianceIssues) { this.complianceIssues = complianceIssues; }
    }
}
