package com.tss.aml.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tss.aml.dto.response.ApiResponseDto;
import com.tss.aml.dto.response.KycDocumentResponseDto;
import com.tss.aml.dto.response.KycStatusSummaryDto;
import com.tss.aml.entity.KycDocument;
import com.tss.aml.service.KycComplianceReportService;
import com.tss.aml.service.KycComplianceReportService.KycComplianceDashboard;
import com.tss.aml.service.KycRuleEvaluator.KycComplianceReport;

@RestController
@RequestMapping("/api/kyc/compliance")
public class KycComplianceController {

    @Autowired
    private KycComplianceReportService kycComplianceReportService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponseDto<KycComplianceDashboard>> getComplianceDashboard() {
        try {
            KycComplianceDashboard dashboard = kycComplianceReportService.getComplianceDashboard();
            
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "Compliance dashboard retrieved successfully", 
                dashboard
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(
                false, 
                "Failed to retrieve compliance dashboard: " + e.getMessage(), 
                null
            ));
        }
    }

    @GetMapping("/customers/status")
    public ResponseEntity<ApiResponseDto<List<KycStatusSummaryDto>>> getAllCustomersKycStatus() {
        try {
            List<KycStatusSummaryDto> statusList = kycComplianceReportService.getAllCustomersKycStatus();
            
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "All customers KYC status retrieved successfully", 
                statusList
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(
                false, 
                "Failed to retrieve customers KYC status: " + e.getMessage(), 
                null
            ));
        }
    }

    @GetMapping("/customer/{customerId}/detailed-report")
    public ResponseEntity<ApiResponseDto<KycComplianceReport>> getDetailedComplianceReport(
            @PathVariable Long customerId) {
        
        try {
            KycComplianceReport report = kycComplianceReportService.getDetailedComplianceReport(customerId);
            
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "Detailed compliance report retrieved successfully", 
                report
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(
                false, 
                "Failed to retrieve detailed compliance report: " + e.getMessage(), 
                null
            ));
        }
    }

    @GetMapping("/documents/attention-required")
    public ResponseEntity<ApiResponseDto<List<KycDocument>>> getDocumentsNeedingAttention() {
        try {
            List<KycDocument> documents = kycComplianceReportService.getDocumentsNeedingAttention();
            
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "Documents needing attention retrieved successfully", 
                documents
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(
                false, 
                "Failed to retrieve documents needing attention: " + e.getMessage(), 
                null
            ));
        }
    }
}
