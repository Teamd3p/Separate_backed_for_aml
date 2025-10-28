package com.tss.aml.service;

import java.util.List;

import com.tss.aml.dto.request.HelpDeskTicketRequest;
import com.tss.aml.entity.HelpDeskTicket;

public interface HelpDeskService {
    HelpDeskTicket createTicket(Long customerId, HelpDeskTicketRequest request);
    List<HelpDeskTicket> getCustomerTickets(Long customerId);
    HelpDeskTicket updateTicketDescription(Long ticketId, Long customerId, String description);
    List<HelpDeskTicket> getAllTickets(String status);
    HelpDeskTicket assignTicket(Long ticketId, Long adminId);
    HelpDeskTicket resolveTicket(Long ticketId, String resolution);
    void deleteTicket(Long ticketId);
}
