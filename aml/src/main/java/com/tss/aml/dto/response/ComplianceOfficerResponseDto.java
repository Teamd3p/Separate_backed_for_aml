package com.tss.aml.dto.response;

import java.time.LocalDateTime;

import com.tss.aml.entity.enums.UserStatus;

public class ComplianceOfficerResponseDto {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UserStatus status;
    private String department;
    private Integer assignedAlertsCount;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;

    // Constructors
    public ComplianceOfficerResponseDto() {}

    public ComplianceOfficerResponseDto(Long userId, String email, String firstName, String lastName,
                                       String phoneNumber, UserStatus status, String department,
                                       Integer assignedAlertsCount, LocalDateTime lastLoginAt,
                                       LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.department = department;
        this.assignedAlertsCount = assignedAlertsCount;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getAssignedAlertsCount() { return assignedAlertsCount; }
    public void setAssignedAlertsCount(Integer assignedAlertsCount) { this.assignedAlertsCount = assignedAlertsCount; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
