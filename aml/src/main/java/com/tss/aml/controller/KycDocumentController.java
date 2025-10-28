package com.tss.aml.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tss.aml.dto.request.KycDocumentVerificationRequest;
import com.tss.aml.dto.response.ApiResponseDto;
import com.tss.aml.dto.response.KycDocumentResponseDto;
import com.tss.aml.dto.response.KycDocumentSummaryDto;
import com.tss.aml.dto.response.KycStatusSummaryDto;
import com.tss.aml.entity.KycDocument;
import com.tss.aml.entity.enums.DocumentType;
import com.tss.aml.entity.enums.KycStatus;
import com.tss.aml.service.KycDocumentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/kyc")
public class KycDocumentController {

	@Autowired
	private KycDocumentService kycDocumentService;

	@PostMapping("/upload")
	public ResponseEntity<ApiResponseDto<KycDocumentResponseDto>> uploadDocument(
			@RequestParam("file") MultipartFile file, @RequestParam("customerId") Long customerId,
			@RequestParam("documentType") DocumentType documentType) {

		try {
			KycDocument document = kycDocumentService.uploadDocument(customerId, documentType, file);
			KycDocumentResponseDto responseDto = convertToResponseDto(document);

			return ResponseEntity.ok(new ApiResponseDto<>(true, "Document uploaded successfully", responseDto));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(new ApiResponseDto<>(false, "Failed to upload document: " + e.getMessage(), null));
		}
	}

	@PostMapping("/verify")
	public ResponseEntity<ApiResponseDto<KycDocumentResponseDto>> verifyDocument(
			@Valid @RequestBody KycDocumentVerificationRequest request) {

		try {
			KycDocument document = kycDocumentService.verifyDocument(request.getDocumentId(), request.getOfficerId(),
					request.getStatus(), request.getVerificationNotes());

			KycDocumentResponseDto responseDto = convertToResponseDto(document);

			return ResponseEntity.ok(new ApiResponseDto<>(true, "Document verification completed", responseDto));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(new ApiResponseDto<>(false, "Failed to verify document: " + e.getMessage(), null));
		}
	}

	@GetMapping("/customer/{customerId}")
	public ResponseEntity<ApiResponseDto<List<KycDocumentSummaryDto>>> getCustomerDocuments(
			@PathVariable Long customerId) {

		try {
			List<KycDocument> documents = kycDocumentService.getCustomerDocuments(customerId);
			List<KycDocumentSummaryDto> summaryDtos = documents.stream().map(this::convertToSummaryDto)
					.collect(Collectors.toList());

			return ResponseEntity
					.ok(new ApiResponseDto<>(true, "Customer documents retrieved successfully", summaryDtos));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
					new ApiResponseDto<>(false, "Failed to retrieve customer documents: " + e.getMessage(), null));
		}
	}

	@GetMapping("/customer/{customerId}/status")
	public ResponseEntity<ApiResponseDto<KycStatusSummaryDto>> getCustomerKycStatus(@PathVariable Long customerId) {

		try {
			List<KycDocument> documents = kycDocumentService.getCustomerDocuments(customerId);
			boolean isKycComplete = kycDocumentService.isCustomerKycComplete(customerId);

			KycStatusSummaryDto statusSummary = new KycStatusSummaryDto();
			statusSummary.setCustomerId(customerId);
			statusSummary.setKycComplete(isKycComplete);
			statusSummary.setTotalDocuments(documents.size());
			statusSummary.setVerifiedDocuments(
					(int) documents.stream().filter(d -> d.getStatus() == KycStatus.VERIFIED).count());
			statusSummary.setPendingDocuments(
					(int) documents.stream().filter(d -> d.getStatus() == KycStatus.PENDING).count());
			statusSummary.setRejectedDocuments(
					(int) documents.stream().filter(d -> d.getStatus() == KycStatus.REJECTED).count());
			statusSummary.setHighRiskDocuments(
					(int) documents.stream().filter(d -> d.getRiskScore() != null && d.getRiskScore() >= 70).count());
			statusSummary.setOverallKycStatus(isKycComplete ? "COMPLETE" : "INCOMPLETE");

			if (!documents.isEmpty() && documents.get(0).getCustomer() != null) {
				statusSummary.setCustomerName(documents.get(0).getCustomer().getFirstName() + " "
						+ documents.get(0).getCustomer().getLastName());
			}

			return ResponseEntity.ok(new ApiResponseDto<>(true, "KYC status retrieved successfully", statusSummary));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(new ApiResponseDto<>(false, "Failed to retrieve KYC status: " + e.getMessage(), null));
		}
	}

	@GetMapping("/pending")
	@PreAuthorize("hasRole('ADMIN') or hasRole('COMPLIANCE_OFFICER')")
	public ResponseEntity<ApiResponseDto<List<KycDocumentResponseDto>>> getPendingDocuments() {
		try {
			List<KycDocument> documents = kycDocumentService.getPendingDocuments();
			List<KycDocumentResponseDto> responseDtos = documents.stream().map(this::convertToResponseDto)
					.collect(Collectors.toList());

			return ResponseEntity
					.ok(new ApiResponseDto<>(true, "Pending documents retrieved successfully", responseDtos));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(new ApiResponseDto<>(false, "Failed to retrieve pending documents: " + e.getMessage(), null));
		}
	}

	@GetMapping("/manual-review")
	@PreAuthorize("hasRole('ADMIN') or hasRole('COMPLIANCE_OFFICER')")
	public ResponseEntity<ApiResponseDto<List<KycDocumentResponseDto>>> getDocumentsRequiringManualReview() {
		try {
			List<KycDocument> documents = kycDocumentService.getDocumentsRequiringManualReview();
			List<KycDocumentResponseDto> responseDtos = documents.stream().map(this::convertToResponseDto)
					.collect(Collectors.toList());

			return ResponseEntity.ok(new ApiResponseDto<>(true,
					"Documents requiring manual review retrieved successfully", responseDtos));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ApiResponseDto<>(false,
					"Failed to retrieve documents requiring manual review: " + e.getMessage(), null));
		}
	}

	@GetMapping("/high-risk")
	@PreAuthorize("hasRole('ADMIN') or hasRole('COMPLIANCE_OFFICER')")
	public ResponseEntity<ApiResponseDto<List<KycDocumentResponseDto>>> getHighRiskDocuments(
			@RequestParam(defaultValue = "70") Integer minRiskScore) {

		try {
			List<KycDocument> documents = kycDocumentService.getHighRiskDocuments(minRiskScore);
			List<KycDocumentResponseDto> responseDtos = documents.stream().map(this::convertToResponseDto)
					.collect(Collectors.toList());

			return ResponseEntity
					.ok(new ApiResponseDto<>(true, "High risk documents retrieved successfully", responseDtos));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
					new ApiResponseDto<>(false, "Failed to retrieve high risk documents: " + e.getMessage(), null));
		}
	}

	@GetMapping("/expiring")
	@PreAuthorize("hasRole('ADMIN') or hasRole('COMPLIANCE_OFFICER')")
	public ResponseEntity<ApiResponseDto<List<KycDocumentResponseDto>>> getExpiringDocuments(
			@RequestParam(defaultValue = "30") Integer daysAhead) {

		try {
			List<KycDocument> documents = kycDocumentService.getExpiringDocuments(daysAhead);
			List<KycDocumentResponseDto> responseDtos = documents.stream().map(this::convertToResponseDto)
					.collect(Collectors.toList());

			return ResponseEntity
					.ok(new ApiResponseDto<>(true, "Expiring documents retrieved successfully", responseDtos));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
					new ApiResponseDto<>(false, "Failed to retrieve expiring documents: " + e.getMessage(), null));
		}
	}

	@GetMapping("/officer/{officerId}")
	public ResponseEntity<ApiResponseDto<List<KycDocumentResponseDto>>> getDocumentsByOfficer(
			@PathVariable Long officerId) {

		try {
			List<KycDocument> documents = kycDocumentService.getDocumentsByOfficer(officerId);
			List<KycDocumentResponseDto> responseDtos = documents.stream().map(this::convertToResponseDto)
					.collect(Collectors.toList());

			return ResponseEntity
					.ok(new ApiResponseDto<>(true, "Documents by officer retrieved successfully", responseDtos));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
					new ApiResponseDto<>(false, "Failed to retrieve documents by officer: " + e.getMessage(), null));
		}
	}

	@GetMapping("/{documentId}")
	public ResponseEntity<ApiResponseDto<KycDocumentResponseDto>> getDocument(@PathVariable Long documentId) {
		try {
			KycDocument document = kycDocumentService.getDocumentById(documentId);
			KycDocumentResponseDto responseDto = convertToResponseDto(document);
			
			return ResponseEntity.ok(new ApiResponseDto<>(true, "Document retrieved successfully", responseDto));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(new ApiResponseDto<>(false, "Failed to retrieve document: " + e.getMessage(), null));
		}
	}

	@PutMapping("/{documentId}/risk-score")
	public ResponseEntity<ApiResponseDto<KycDocumentResponseDto>> updateRiskScore(@PathVariable Long documentId,
			@RequestParam Integer riskScore) {

		try {
			KycDocument document = kycDocumentService.updateDocumentRiskScore(documentId, riskScore);
			KycDocumentResponseDto responseDto = convertToResponseDto(document);

			return ResponseEntity.ok(new ApiResponseDto<>(true, "Risk score updated successfully", responseDto));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(new ApiResponseDto<>(false, "Failed to update risk score: " + e.getMessage(), null));
		}
	}

	@DeleteMapping("/{documentId}")
	public ResponseEntity<ApiResponseDto<Void>> deleteDocument(@PathVariable Long documentId) {
		try {
			kycDocumentService.deleteDocument(documentId);

			return ResponseEntity.ok(new ApiResponseDto<>(true, "Document deleted successfully", null));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(new ApiResponseDto<>(false, "Failed to delete document: " + e.getMessage(), null));
		}
	}

	@GetMapping("/reports/date-range")
	public ResponseEntity<ApiResponseDto<List<KycDocumentResponseDto>>> getDocumentsByDateRange(
			@RequestParam String startDate, @RequestParam String endDate) {

		try {
			LocalDateTime start = LocalDateTime.parse(startDate);
			LocalDateTime end = LocalDateTime.parse(endDate);

			List<KycDocument> documents = kycDocumentService.getDocumentsByDateRange(start, end);
			List<KycDocumentResponseDto> responseDtos = documents.stream().map(this::convertToResponseDto)
					.collect(Collectors.toList());

			return ResponseEntity
					.ok(new ApiResponseDto<>(true, "Documents by date range retrieved successfully", responseDtos));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
					new ApiResponseDto<>(false, "Failed to retrieve documents by date range: " + e.getMessage(), null));
		}
	}

	private KycDocumentResponseDto convertToResponseDto(KycDocument document) {
		KycDocumentResponseDto dto = new KycDocumentResponseDto();
		dto.setId(document.getId());
		dto.setCustomerId(document.getCustomer().getUserId());
		dto.setCustomerName(document.getCustomer().getFirstName() + " " + document.getCustomer().getLastName());
		dto.setDocumentType(document.getDocType());
		dto.setStatus(document.getStatus());
		dto.setFileName(document.getFileName());
		dto.setFileSize(document.getFileSize());
		dto.setDocumentNumber(document.getDocumentNumber());

		dto.setVerificationNotes(document.getVerificationNotes());

		if (document.getVerifiedBy() != null) {
			dto.setVerifiedByName(
					document.getVerifiedBy().getFirstName() + " " + document.getVerifiedBy().getLastName());
		}

		dto.setUploadTimestamp(document.getUploadTimestamp());
		dto.setVerificationTimestamp(document.getVerificationTimestamp());
		dto.setRiskScore(document.getRiskScore());
		dto.setValidated(document.isValidated());

		return dto;
	}

	private KycDocumentSummaryDto convertToSummaryDto(KycDocument document) {
		KycDocumentSummaryDto dto = new KycDocumentSummaryDto();
		dto.setId(document.getId());
		dto.setDocumentType(document.getDocType());
		dto.setStatus(document.getStatus());
		dto.setFileName(document.getFileName());
		dto.setUploadTimestamp(document.getUploadTimestamp());
		dto.setRiskScore(document.getRiskScore());

		return dto;
	}
}
