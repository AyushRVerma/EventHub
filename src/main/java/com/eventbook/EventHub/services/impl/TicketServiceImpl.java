package com.eventbook.EventHub.services.impl;

import com.eventbook.EventHub.domain.entity.Ticket;
import com.eventbook.EventHub.repositories.TicketRepository;
import com.eventbook.EventHub.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import com.eventbook.EventHub.domain.DTOs.TicketCancellationResponseDto;
import com.eventbook.EventHub.domain.entity.TicketStatusEnum;
import com.eventbook.EventHub.exceptions.TicketNotFoundException;
import com.eventbook.EventHub.messaging.RabbitMQProducer;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final RabbitMQProducer rabbitMQProducer;

    @Override
    public Page<Ticket> listTicketForUser(UUID userId, Pageable pageable) {
        return ticketRepository.findByPurchaserId(userId,pageable);
    }

    @Override
    public Optional<Ticket> getTicketForUser(UUID userId,UUID ticketId) {
      return ticketRepository.findByIdAndPurchaserId(ticketId,userId);
    }

    @Override
    @Transactional
    public TicketCancellationResponseDto cancelTicket(UUID userId, UUID ticketId) {
        Ticket ticket = ticketRepository.findByIdAndPurchaserId(ticketId, userId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found for current user"));

        if (ticket.getStatus() == TicketStatusEnum.CANCELLED || ticket.getStatus() == TicketStatusEnum.EXPIRED) {
            throw new IllegalStateException("Ticket is already " + ticket.getStatus());
        }

        // Mark as CANCELLED
        ticket.setStatus(TicketStatusEnum.CANCELLED);
        Ticket savedTicket = ticketRepository.save(ticket);

        // Publish TicketReleasedEvent to RabbitMQ to trigger Waitlist Processor!
        if (savedTicket.getTicketType() != null) {
            rabbitMQProducer.sendTicketReleasedEvent(savedTicket.getTicketType().getId());
        }

        return TicketCancellationResponseDto.builder()
                .ticketId(savedTicket.getId())
                .status(savedTicket.getStatus())
                .message("Ticket cancelled successfully. Refund initiated to original payment method.")
                .build();
    }
}
