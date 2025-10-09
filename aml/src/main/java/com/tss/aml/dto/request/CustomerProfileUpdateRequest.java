package com.tss.aml.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileUpdateRequest {
    @NotBlank(message = "First name is required")
    private String firstName;
    
    private String middleName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;
    
    private String street;
    private String city;
    private String state;
    private String nation;
    private String pincode;
    
    @NotBlank(message = "Contact number is required")
    private String contactNumber;
    
    @NotBlank(message = "OTP is required for profile update")
    private String otp;
    
    @NotBlank(message = "Email is required for OTP verification")
    private String email;
    
    private String nationality;
}
