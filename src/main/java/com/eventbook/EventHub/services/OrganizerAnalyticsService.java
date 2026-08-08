package com.eventbook.EventHub.services;

import com.eventbook.EventHub.domain.DTOs.OrganizerAnalyticsResponseDto;

import java.util.UUID;

public interface OrganizerAnalyticsService {

    OrganizerAnalyticsResponseDto getAnalyticsForOrganizer(UUID organizerId);
}
