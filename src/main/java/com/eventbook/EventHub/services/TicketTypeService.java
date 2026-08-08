package com.eventbook.EventHub.services;

import com.eventbook.EventHub.domain.entity.Ticket;

import java.util.UUID;

import com.eventbook.EventHub.domain.DTOs.CreateRazorpayOrderResponseDto;

public interface TicketTypeService {

    CreateRazorpayOrderResponseDto purchaseTicket(UUID userId , UUID ticketTypeId ,UUID eventId);
}
