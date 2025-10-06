package com.tss.aml.entity;

import java.time.LocalDateTime;

import com.tss.aml.entity.enums.DocumentType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kyc_documents")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KycDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@NotNull
	@Enumerated(EnumType.STRING)
	private DocumentType docType;

	@NotNull
	private String fileUrl; // Cloudinary/S3 URL

	@Lob // For large text (OCR output)
	private String extractedText; // ← NEW FIELD

	private LocalDateTime uploadTimestamp = LocalDateTime.now();
	private boolean isValidated = false;

	public KycDocument(Customer customer, DocumentType docType, String fileUrl) {
		this.customer = customer;
		this.docType = docType;
		this.fileUrl = fileUrl;
	}


}