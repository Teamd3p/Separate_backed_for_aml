package com.tss.aml.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.aml.dto.request.InvestigationActionRequest;
import com.tss.aml.dto.request.SarRequest;
import com.tss.aml.entity.Alert;
import com.tss.aml.entity.ComplianceOfficer;
import com.tss.aml.entity.Sar;
import com.tss.aml.entity.Sar.SarStatus;
import com.tss.aml.entity.Transaction;
import com.tss.aml.entity.enums.AlertStatus;
import com.tss.aml.entity.enums.Role;
import com.tss.aml.entity.enums.TransactionStatus;
import com.tss.aml.repository.AlertRepository;
import com.tss.aml.repository.ComplianceOfficerRepository;
import com.tss.aml.repository.SarRepository;
import com.tss.aml.repository.TransactionRepository;
import com.tss.aml.service.ComplianceOfficerService;

@Service
@Transactional
public class ComplianceOfficerServiceImpl implements ComplianceOfficerService {

	@Autowired
	private AlertRepository alertRepo;

	@Autowired
	private ComplianceOfficerRepository officerRepo;

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private SarRepository sarRepo;

	@Autowired
	private com.tss.aml.service.OtpService otpService;

	@Autowired
	private com.tss.aml.service.EmailService emailService;

	// === ALERTS ===
	@Override
	public List<Alert> getAllAlerts() {
		return alertRepo.findAll();
	}

	@Override
	public Alert assignAlertToOfficer(Long alertId, Long officerId) {
		Alert alert = alertRepo.findById(alertId).orElseThrow(() -> new RuntimeException("Alert not found"));
		ComplianceOfficer officer = officerRepo.findById(officerId)
				.orElseThrow(() -> new RuntimeException("Officer not found"));
		alert.setAssignedTo(officer);
		alert.setInvestigationStatus(Alert.InvestigationStatus.INVESTIGATING);
		return alertRepo.save(alert);
	}

	@Override
	public Alert getAlertDetails(Long alertId) {
		return alertRepo.findById(alertId).orElseThrow(() -> new RuntimeException("Alert not found"));
	}

	@Override
	public List<Transaction> getCustomerTransactions(Long customerId) {
		return transactionRepo.findByCustomerUserId(customerId);
	}

	// === INVESTIGATION ===
	@Override
	public Alert takeActionOnAlert(Long alertId, Long officerId, InvestigationActionRequest request) {
		Alert alert = alertRepo.findById(alertId).orElseThrow(() -> new RuntimeException("Alert not found"));
		ComplianceOfficer officer = officerRepo.findById(officerId)
				.orElseThrow(() -> new RuntimeException("Officer not found"));

		ComplianceOfficer assignedOfficer = alert.getAssignedTo();
		if (assignedOfficer != null && !officer.getUserId().equals(assignedOfficer.getUserId())
				&& !officer.getRole().equals(Role.ADMIN)) {
			throw new RuntimeException("Not authorized...");
		}

		Alert.InvestigationStatus investigationStatus = Alert.InvestigationStatus.valueOf(request.getDecision());
		alert.setInvestigationStatus(investigationStatus);

		// Update alert status based on investigation decision
		if (investigationStatus == Alert.InvestigationStatus.TRUE_POSITIVE) {
			alert.setStatus(AlertStatus.TRUE_POSITIVE);
			alert.getTransaction().setStatus(TransactionStatus.BLOCKED);
		} else if (investigationStatus == Alert.InvestigationStatus.FALSE_POSITIVE) {
			alert.setStatus(AlertStatus.FALSE_POSITIVE);
			alert.getTransaction().setStatus(TransactionStatus.COMPLETED);
		} else if (investigationStatus == Alert.InvestigationStatus.ESCALATED) {
			alert.setStatus(AlertStatus.ESCALATED);
		} else {
			alert.setStatus(AlertStatus.INVESTIGATING);
		}

		// Auto-generate SAR if true positive
		if (investigationStatus == Alert.InvestigationStatus.TRUE_POSITIVE && request.getSarSummary() != null) {
			Sar sar = new Sar(alert, officer, request.getSarSummary());
			sarRepo.save(sar);
		}

		return alertRepo.save(alert);
	}

	// === SAR ===
	@Override
	public Sar generateSar(Long alertId, Long officerId, SarRequest request) {
		Alert alert = alertRepo.findById(alertId).orElseThrow(() -> new RuntimeException("Alert not found"));
		ComplianceOfficer officer = officerRepo.findById(officerId)
				.orElseThrow(() -> new RuntimeException("Officer not found"));
		Sar sar = new Sar(alert, officer, request.getSummary());
		return sarRepo.save(sar);
	}

	@Override
	public Sar submitSar(Long sarId) {
		Sar sar = sarRepo.findById(sarId).orElseThrow(() -> new RuntimeException("SAR not found"));
		sar.setSubmittedAt(LocalDateTime.now());
		sar.setStatus(SarStatus.SUBMITTED);
		return sarRepo.save(sar);
	}

	// New methods for enhanced compliance officer endpoints
	@Override
	public List<Alert> getAlertsByStatus(AlertStatus status) {
		return alertRepo.findByStatusOrderByCreatedAtDesc(status);
	}

	@Override
	public List<Alert> getAlertsByRiskScoreRange(Integer minRiskScore, Integer maxRiskScore) {
		return alertRepo.findByRiskScoreBetweenOrderByRiskScoreDesc(minRiskScore, maxRiskScore);
	}

	@Override
	public List<Sar> getAllSars() {
		return sarRepo.findAllByOrderByCreatedAtDesc();
	}

	@Override
	public List<String> getTriggeredRulesForAlert(Long alertId) {
		Alert alert = alertRepo.findById(alertId).orElseThrow(() -> new RuntimeException("Alert not found"));

		String ruleTriggered = alert.getRuleTriggered();
		if (ruleTriggered != null && !ruleTriggered.isEmpty()) {
			return List.of(ruleTriggered.split("[,;]"));
		}
		return List.of();
	}

	@Override
	public List<Alert> getAlertHistoryByCustomerId(Long customerId) {
		return alertRepo.findByCustomerUserIdOrderByCreatedAtDesc(customerId);
	}

	@Override
	public List<Alert> getAlertHistoryByOfficerId(Long officerId) {
		return alertRepo.findByAssignedToUserIdOrderByCreatedAtDesc(officerId);
	}

	@Override
	public com.tss.aml.dto.response.OfficerProfileDto getOfficerProfile(Long officerId) {
		ComplianceOfficer officer = officerRepo.findById(officerId)
				.orElseThrow(() -> new RuntimeException("Officer not found"));

		com.tss.aml.dto.response.OfficerProfileDto dto = new com.tss.aml.dto.response.OfficerProfileDto();
		dto.setOfficerId(officer.getUserId());
		dto.setFirstName(officer.getFirstName());
		dto.setLastName(officer.getLastName());
		dto.setEmail(officer.getEmail());
		dto.setPhoneNumber(officer.getPhone());
	
		dto.setStatus(officer.getStatus());
		dto.setCreatedAt(officer.getCreatedAt());
		dto.setLastLoginAt(officer.getLastLogin());

		return dto;
	}

	@Override
	public com.tss.aml.dto.response.OfficerProfileDto updateOfficerProfile(Long officerId,
			com.tss.aml.dto.request.OfficerProfileUpdateRequest request) {
		// Verify OTP first
		if (!otpService.verifyOtp(request.getEmail(), request.getOtp())) {
			throw new RuntimeException("Invalid OTP");
		}

		ComplianceOfficer officer = officerRepo.findById(officerId)
				.orElseThrow(() -> new RuntimeException("Officer not found"));

		if (request.getFirstName() != null) {
			officer.setFirstName(request.getFirstName());
		}
		if (request.getLastName() != null) {
			officer.setLastName(request.getLastName());
		}
		if (request.getPhoneNumber() != null) {
			officer.setPhone(request.getPhoneNumber());
		}
	
		officer = officerRepo.save(officer);
		return getOfficerProfile(officer.getUserId());
	}

	@Override
	public void sendOfficerProfileUpdateOtp(String email) {
		String otp = otpService.generateOtp(email);
		emailService.sendOtpEmail(email, otp);
	}

	@Override
	public void sendProfileUpdateOtp(String email) {
		String otp = otpService.generateOtp(email);
		emailService.sendOtpEmail(email, otp);		
	}
}