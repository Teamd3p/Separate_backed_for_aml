package com.tss.aml.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
import com.tss.aml.entity.enums.TicketStatus;
import com.tss.aml.entity.enums.TicketPriority;

@Entity
@Table(name = "helpdesk_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelpDeskTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @Column(length = 200)
    private String subject;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TicketStatus status = TicketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TicketPriority priority = TicketPriority.MEDIUM;

    @Column(name = "assigned_to_id")
    private Long assignedToId;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime resolvedAt;
}
