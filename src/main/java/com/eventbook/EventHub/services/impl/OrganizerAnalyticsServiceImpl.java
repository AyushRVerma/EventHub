package com.eventbook.EventHub.services.impl;

import com.eventbook.EventHub.domain.DTOs.OrganizerAnalyticsResponseDto;
import com.eventbook.EventHub.domain.entity.EventStatusEnum;
import com.eventbook.EventHub.domain.entity.TicketStatusEnum;
import com.eventbook.EventHub.domain.entity.WaitlistStatusEnum;
import com.eventbook.EventHub.repositories.EventRepository;
import com.eventbook.EventHub.repositories.TicketRepository;
import com.eventbook.EventHub.repositories.WaitlistRepository;
import com.eventbook.EventHub.services.OrganizerAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizerAnalyticsServiceImpl implements OrganizerAnalyticsService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final WaitlistRepository waitlistRepository;

    @Override
    public OrganizerAnalyticsResponseDto getAnalyticsForOrganizer(UUID organizerId) {
        long totalEvents = eventRepository.countByOrganizerId(organizerId);
        long publishedEvents = eventRepository.countByOrganizerIdAndStatus(organizerId, EventStatusEnum.PUBLISHED);
        long draftEvents = eventRepository.countByOrganizerIdAndStatus(organizerId, EventStatusEnum.DRAFT);

        long totalTicketsSold = ticketRepository.countByTicketTypeEventOrganizerIdAndStatus(organizerId, TicketStatusEnum.PURCHASED);

        Double revenueObj = ticketRepository.calculateTotalRevenueForOrganizer(organizerId);
        double totalRevenue = revenueObj != null ? revenueObj : 0.0;

        long totalWaitlist = waitlistRepository.countByTicketTypeEventOrganizerIdAndStatus(organizerId, WaitlistStatusEnum.WAITING);

        Long validatedObj = ticketRepository.countValidatedTicketsForOrganizer(organizerId);
        long totalValidated = validatedObj != null ? validatedObj : 0L;

        log.info("Calculated analytics for organizer {}: {} events, {} sold, ₹{} revenue",
                organizerId, totalEvents, totalTicketsSold, totalRevenue);

        return OrganizerAnalyticsResponseDto.builder()
                .totalEvents(totalEvents)
                .publishedEvents(publishedEvents)
                .draftEvents(draftEvents)
                .totalTicketsSold(totalTicketsSold)
                .totalRevenueINR(totalRevenue)
                .totalWaitlistCount(totalWaitlist)
                .totalTicketsValidated(totalValidated)
                .build();
    }
}
