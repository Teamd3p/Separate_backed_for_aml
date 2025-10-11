package com.tss.aml.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelpDeskTicketDto {
	private Long ticketId;
	private Long customerId;
	private String customerName;
	private String subject;
	private String description;
	private String status;
	private String priority;
	private Long assignedToId;
	private String resolution;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime resolvedAt;

}
