package com.eventbook.EventHub.services;

import com.eventbook.EventHub.domain.entity.TicketValidation;

import java.util.UUID;

public interface TicketValidationService {

    TicketValidation validateTicketByQrCode(UUID qrCodeId);
    TicketValidation validateTicketByManually(UUID ticketId);
}
