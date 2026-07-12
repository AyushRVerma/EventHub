package com.eventbook.EventHub.services;

import com.eventbook.EventHub.domain.entity.Ticket;

import java.util.UUID;

public interface TicketTypeService {

    Ticket purchaseTicket(UUID userId , UUID ticketTypeId);
}
