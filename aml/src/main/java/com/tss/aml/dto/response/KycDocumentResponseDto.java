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
    private KycStatus status;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String documentNumber;
    private LocalDateTime expiryDate;
    private LocalDateTime issueDate;
    private String issuingAuthority;
    private String verificationNotes;
    private String verifiedByName;
    private LocalDateTime uploadTimestamp;
    private LocalDateTime verificationTimestamp;
    private Double confidenceScore;
    private Integer riskScore;
    private boolean isValidated;
    private boolean requiresManualReview;
}
