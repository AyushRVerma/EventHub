package com.eventbook.EventHub.services;

import com.eventbook.EventHub.domain.DTOs.WaitlistResponseDto;

import java.util.UUID;

public interface WaitlistService {

    WaitlistResponseDto joinWaitlist(UUID userId, UUID ticketTypeId);

    void processWaitlistOnTicketRelease(UUID ticketTypeId);
}
