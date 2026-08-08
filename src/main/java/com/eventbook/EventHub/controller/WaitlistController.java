package com.eventbook.EventHub.controller;

import com.eventbook.EventHub.domain.DTOs.WaitlistResponseDto;
import com.eventbook.EventHub.services.WaitlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.eventbook.EventHub.util.JwtUtil.parseUserId;

@RestController
@RequestMapping("/api/v1/ticket-types")
@RequiredArgsConstructor
public class WaitlistController {

    private final WaitlistService waitlistService;

    @PostMapping("/{ticketTypeId}/waitlist")
    public ResponseEntity<WaitlistResponseDto> joinWaitlist(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID ticketTypeId) {

        UUID userId = parseUserId(jwt);
        WaitlistResponseDto response = waitlistService.joinWaitlist(userId, ticketTypeId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
