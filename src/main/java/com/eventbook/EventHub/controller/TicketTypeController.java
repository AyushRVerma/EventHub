package com.eventbook.EventHub.controller;

import com.eventbook.EventHub.services.TicketTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.eventbook.EventHub.util.JwtUtil.parseUserId;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events/{eventId}/ticket-types")
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    @PostMapping("/{ticketTypeId}/tickets")
    public ResponseEntity<Void> purchaseTicket
            (@AuthenticationPrincipal Jwt jwt,
             @PathVariable UUID ticketTypeId){
        ticketTypeService.purchaseTicket(parseUserId(jwt), ticketTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT );
    }
}
