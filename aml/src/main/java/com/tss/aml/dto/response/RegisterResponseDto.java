package com.tss.aml.dto.response;

import com.tss.aml.entity.enums.UserStatus;

public class RegisterResponseDto {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private UserStatus status;
    private String message;

    // Constructors
    public RegisterResponseDto() {}

    public RegisterResponseDto(Long userId, String email, String firstName, String lastName, 
                              UserStatus status, String message) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.message = message;
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

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
