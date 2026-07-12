package com.eventbook.EventHub.services;

import com.eventbook.EventHub.domain.entity.QrCode;
import com.eventbook.EventHub.domain.entity.Ticket;

import java.util.UUID;

public interface QrCodeService {

    QrCode generateQrCode(Ticket ticket);

    byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId);
}
