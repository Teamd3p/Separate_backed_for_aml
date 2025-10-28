package com.tss.aml.dto.response;

import java.time.LocalDateTime;

import com.tss.aml.entity.enums.DocumentType;
import com.tss.aml.entity.enums.KycStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KycDocumentResponseDto {
	private Long id;
	private Long customerId;
	private String customerName;
	private DocumentType documentType;
	private String fileName;
	private Long fileSize;
	private String documentNumber;
	private KycStatus status;
	private LocalDateTime uploadTimestamp;
	private LocalDateTime verificationTimestamp;
	private String verificationNotes;
	private String verifiedByName;
	private Integer riskScore;
	private Double confidenceScore;
	private boolean requiresManualReview;
	private boolean validated;

}
