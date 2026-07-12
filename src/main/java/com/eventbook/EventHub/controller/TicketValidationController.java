package com.eventbook.EventHub.controller;

import com.eventbook.EventHub.domain.DTOs.TicketValidationRequestDto;
import com.eventbook.EventHub.domain.DTOs.TicketValidationResponseDto;
import com.eventbook.EventHub.domain.entity.TicketValidation;
import com.eventbook.EventHub.domain.entity.TicketValidationMethod;
import com.eventbook.EventHub.mappers.TicketValidationMapper;
import com.eventbook.EventHub.services.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ticket-validations")
@RequiredArgsConstructor
public class TicketValidationController {

    private final TicketValidationService ticketValidationService;
    private final TicketValidationMapper ticketValidationMapper;

    @PostMapping
    public ResponseEntity<TicketValidationResponseDto> validateTicket(
            @RequestBody TicketValidationRequestDto ticketValidationRequestDto
    ){
        TicketValidationMethod method = ticketValidationRequestDto.getMethod();
        TicketValidation ticketValidation;
        if(TicketValidationMethod.MANUAL.equals(method)) {

            ticketValidation = ticketValidationService.validateTicketByManually(
                    ticketValidationRequestDto.getId()
            );
        }
            else{
                ticketValidation = ticketValidationService.validateTicketByQrCode(
                        ticketValidationRequestDto.getId()
                );
            }

            return ResponseEntity.ok(ticketValidationMapper.toTicketValidationResponseDto(ticketValidation));
        }

    }


