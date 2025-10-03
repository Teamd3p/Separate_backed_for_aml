package com.tss.aml.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sars")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sarId;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alert_id", nullable = false)
	private Alert alert;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "officer_id", nullable = false)
	private ComplianceOfficer officer;

	@NotNull
	private String summary;

	private String regulatorReference; // e.g., FIU-IND reference

	@Enumerated(EnumType.STRING)
	private SarStatus status = SarStatus.DRAFT;

	private LocalDateTime submittedAt;
	private LocalDateTime createdAt = LocalDateTime.now();

	public Sar(Alert alert, ComplianceOfficer officer, String summary) {
		this.alert = alert;
		this.officer = officer;
		this.summary = summary;
	}

	// Getters & Setters
	public enum SarStatus {
		DRAFT, SUBMITTED, REJECTED
	}

	public SarStatus getStatus() {
		return status;
	}

	public void setStatus(SarStatus status) {
		this.status = status;
	}
}