package com.eventbook.EventHub.domain.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizerAnalyticsResponseDto {
    private Long totalEvents;
    private Long publishedEvents;
    private Long draftEvents;
    private Long totalTicketsSold;
    private Double totalRevenueINR;
    private Long totalWaitlistCount;
    private Long totalTicketsValidated;
}
