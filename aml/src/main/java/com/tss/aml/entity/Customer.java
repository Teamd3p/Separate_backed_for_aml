package com.tss.aml.entity;

import java.time.LocalDate;

import com.tss.aml.entity.enums.KycStatus;
import com.tss.aml.entity.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer extends User {

	@NotNull
	private String firstName;

	private String middleName;

	@NotNull
	private String lastName;

	@NotNull
	private LocalDate dateOfBirth;

	@NotNull
	private String nationality;

	private String street;
	private String city;
	private String state;
	private String nation;
	private String pincode;

	@NotNull
	private String contactNumber;

	@Enumerated(EnumType.STRING)
	private KycStatus kycStatus = KycStatus.PENDING;

	public Customer(String email, String passwordHash, String firstName, String lastName, LocalDate dateOfBirth,
			String nationality, String contactNumber) {
		super(email, passwordHash, Role.CUSTOMER);
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.nationality = nationality;
		this.contactNumber = contactNumber;
	}

}