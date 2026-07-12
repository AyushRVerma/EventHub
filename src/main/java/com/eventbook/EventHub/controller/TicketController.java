package com.eventbook.EventHub.controller;


import com.eventbook.EventHub.domain.DTOs.GetTicketResponseDto;
import com.eventbook.EventHub.domain.DTOs.ListTicketResponseDto;
import com.eventbook.EventHub.mappers.TicketMapper;
import com.eventbook.EventHub.services.QrCodeService;
import com.eventbook.EventHub.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.eventbook.EventHub.util.JwtUtil.parseUserId;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final QrCodeService qrCodeService;


    @GetMapping
    public Page<ListTicketResponseDto> listTickets(
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable){

       return  ticketService.listTicketForUser(
                parseUserId(jwt),
                pageable
        ).map(ticketMapper::toListTicketResponseDto);

    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<GetTicketResponseDto> getTicket(
            @AuthenticationPrincipal Jwt jwt
            ,@PathVariable UUID ticketId){

       return ticketService
                .getTicketForUser(parseUserId(jwt),ticketId)
                .map(ticketMapper::toGetTicketResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{ticketId}/qr-codes")
     public ResponseEntity<byte[]> getQrCodeForTicket(@AuthenticationPrincipal
                                                      Jwt jwt,
                                                      @PathVariable UUID ticketId){

       byte[] qrCodeImage =  qrCodeService.getQrCodeImageForUserAndTicket(parseUserId(jwt),ticketId);

       HttpHeaders headers = new HttpHeaders();
       headers.setContentType(MediaType.IMAGE_PNG);
       headers.setContentLength(qrCodeImage.length);
        headers.setContentDispositionFormData("attachment", "qrcode.png");
       return  ResponseEntity.ok().headers(headers).body(qrCodeImage);

    }
}
