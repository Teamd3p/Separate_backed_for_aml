package com.tss.aml.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.aml.dto.request.HelpDeskTicketRequest;
import com.tss.aml.entity.Customer;
import com.tss.aml.entity.HelpDeskTicket;
import com.tss.aml.entity.enums.TicketStatus;
import com.tss.aml.repository.CustomerRepository;
import com.tss.aml.repository.HelpDeskTicketRepository;
import com.tss.aml.service.HelpDeskService;

@Service
@Transactional
public class HelpDeskServiceImpl implements HelpDeskService {

    @Autowired
    private HelpDeskTicketRepository helpDeskRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public HelpDeskTicket createTicket(Long customerId, HelpDeskTicketRequest request) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        HelpDeskTicket ticket = new HelpDeskTicket();
        ticket.setCustomer(customer);
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        
        return helpDeskRepository.save(ticket);
    }

    @Override
    public List<HelpDeskTicket> getCustomerTickets(Long customerId) {
        return helpDeskRepository.findByCustomerUserIdOrderByCreatedAtDesc(customerId);
    }

    @Override
    public HelpDeskTicket updateTicketDescription(Long ticketId, Long customerId, String description) {
        HelpDeskTicket ticket = helpDeskRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (!ticket.getCustomer().getUserId().equals(customerId)) {
            throw new RuntimeException("Unauthorized to update this ticket");
        }
        
        ticket.setDescription(description);
        ticket.setUpdatedAt(LocalDateTime.now());
        
        return helpDeskRepository.save(ticket);
    }

    @Override
    public List<HelpDeskTicket> getAllTickets(String status) {
        if (status != null && !status.isEmpty()) {
            TicketStatus ticketStatus = TicketStatus.valueOf(status.toUpperCase());
            return helpDeskRepository.findByStatusOrderByCreatedAtDesc(ticketStatus);
        }
        return helpDeskRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public HelpDeskTicket assignTicket(Long ticketId, Long adminId) {
        HelpDeskTicket ticket = helpDeskRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        ticket.setAssignedToId(adminId);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setUpdatedAt(LocalDateTime.now());
        
        return helpDeskRepository.save(ticket);
    }

    @Override
    public HelpDeskTicket resolveTicket(Long ticketId, String resolution) {
        HelpDeskTicket ticket = helpDeskRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        ticket.setResolution(resolution);
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setResolvedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        
        return helpDeskRepository.save(ticket);
    }

    @Override
    public void deleteTicket(Long ticketId) {
        if (!helpDeskRepository.existsById(ticketId)) {
            throw new RuntimeException("Ticket not found");
        }
        helpDeskRepository.deleteById(ticketId);
    }
}
