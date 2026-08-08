package com.eventbook.EventHub.controller;

import com.eventbook.EventHub.domain.DTOs.OrganizerAnalyticsResponseDto;
import com.eventbook.EventHub.services.OrganizerAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.eventbook.EventHub.util.JwtUtil.parseUserId;

@RestController
@RequestMapping("/api/v1/organizer")
@RequiredArgsConstructor
public class OrganizerAnalyticsController {

    private final OrganizerAnalyticsService organizerAnalyticsService;

    @GetMapping("/analytics")
    public ResponseEntity<OrganizerAnalyticsResponseDto> getAnalytics(@AuthenticationPrincipal Jwt jwt) {
        UUID organizerId = parseUserId(jwt);
        OrganizerAnalyticsResponseDto analytics = organizerAnalyticsService.getAnalyticsForOrganizer(organizerId);
        return ResponseEntity.ok(analytics);
    }
}
