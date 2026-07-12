package com.eventbook.EventHub.services.impl;

import com.eventbook.EventHub.domain.entity.*;
import com.eventbook.EventHub.exceptions.QrCodeNotFoundException;
import com.eventbook.EventHub.exceptions.TicketNotFoundException;
import com.eventbook.EventHub.repositories.QrCodeRepository;
import com.eventbook.EventHub.repositories.TicketRepository;
import com.eventbook.EventHub.repositories.TicketValidationRespository;
import com.eventbook.EventHub.services.TicketValidationService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketValidationImpl   implements TicketValidationService{

    private final QrCodeRepository qrCodeRepository;
    private final TicketValidationRespository ticketValidationRespository;
    private final TicketRepository ticketRepository;

    @Override
    public TicketValidation validateTicketByQrCode(UUID qrCodeId) {
       QrCode qrCode = qrCodeRepository.findByIdAndStatus(qrCodeId , QrCodeStatusEnum.ACTIVE)
                .orElseThrow(() -> new QrCodeNotFoundException(
                        String.format("QR Code with id %s not found", qrCodeId)
                ));

        Ticket ticket = qrCode.getTicket();

        return validateTicket(ticket, TicketValidationMethod.QR_SCAN);

    }

    @Nonnull
    private TicketValidation validateTicket(Ticket ticket, TicketValidationMethod ticketValidationMethod) {
        TicketValidation ticketValidation = new TicketValidation();
        ticketValidation.setTicket(ticket);
        ticketValidation.setValidationMethod(ticketValidationMethod);

        TicketValidationStatusEnum ticketValidationStatus = ticket.getValidation()
                .stream()
                .filter(v -> TicketValidationStatusEnum.VALID.equals(v.getStatus()))
                .findFirst()
                .map( v -> TicketValidationStatusEnum.INVALID)
                .orElse((TicketValidationStatusEnum.INVALID));

        ticketValidation.setStatus(ticketValidationStatus);

        return ticketValidationRespository.save(ticketValidation);
    }

    @Override
    public TicketValidation validateTicketByManually(UUID ticketId) {
       Ticket ticket =  ticketRepository.findById(ticketId).orElseThrow(TicketNotFoundException::new);
       return validateTicket(ticket, TicketValidationMethod.MANUAL);
    }
}
