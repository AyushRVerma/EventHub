package com.eventbook.EventHub.services.impl;

import com.eventbook.EventHub.domain.DTOs.WaitlistResponseDto;
import com.eventbook.EventHub.domain.entity.*;
import com.eventbook.EventHub.exceptions.TicketTypeNotFoundException;
import com.eventbook.EventHub.exceptions.UserNotFoundException;
import com.eventbook.EventHub.repositories.TicketRepository;
import com.eventbook.EventHub.repositories.TicketTypeRepository;
import com.eventbook.EventHub.repositories.UserRepository;
import com.eventbook.EventHub.repositories.WaitlistRepository;
import com.eventbook.EventHub.services.WaitlistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitlistServiceImpl implements WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public WaitlistResponseDto joinWaitlist(UUID userId, UUID ticketTypeId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("User with id %s not found", userId))
        );

        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId).orElseThrow(() ->
                new TicketTypeNotFoundException(String.format("Ticket type with id %s not found", ticketTypeId))
        );

        int purchasedCount = ticketRepository.countByTicketTypeId(ticketTypeId);
        if (purchasedCount < ticketType.getTotalAvailable()) {
            throw new IllegalStateException("Tickets are still available. You do not need to join the waitlist.");
        }

        boolean alreadyWaiting = waitlistRepository.existsByTicketTypeIdAndUserIdAndStatus(
                ticketTypeId, userId, WaitlistStatusEnum.WAITING
        );
        if (alreadyWaiting) {
            throw new IllegalStateException("You are already on the waitlist for this ticket type.");
        }

        WaitlistEntry entry = WaitlistEntry.builder()
                .ticketType(ticketType)
                .user(user)
                .status(WaitlistStatusEnum.WAITING)
                .build();

        WaitlistEntry savedEntry = waitlistRepository.save(entry);

        int position = waitlistRepository.countByTicketTypeIdAndStatus(ticketTypeId, WaitlistStatusEnum.WAITING);

        log.info("User {} joined waitlist for ticket type {} at position {}", user.getEmail(), ticketType.getName(), position);

        return WaitlistResponseDto.builder()
                .waitlistId(savedEntry.getId())
                .ticketTypeId(ticketType.getId())
                .ticketTypeName(ticketType.getName())
                .eventName(ticketType.getEvent().getName())
                .positionInQueue(position)
                .status(savedEntry.getStatus())
                .joinedAt(savedEntry.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void processWaitlistOnTicketRelease(UUID ticketTypeId) {
        Optional<WaitlistEntry> nextInLine = waitlistRepository.findFirstByTicketTypeIdAndStatusOrderByCreatedAtAsc(
                ticketTypeId, WaitlistStatusEnum.WAITING
        );

        if (nextInLine.isPresent()) {
            WaitlistEntry entry = nextInLine.get();
            entry.setStatus(WaitlistStatusEnum.NOTIFIED);
            entry.setNotifiedAt(LocalDateTime.now());
            waitlistRepository.save(entry);

            TicketType ticketType = entry.getTicketType();
            User user = entry.getUser();

            emailService.sendWaitlistNotificationEmail(
                    ticketType.getEvent().getName(),
                    ticketType.getName(),
                    user.getEmail()
            );

            log.info("Notified next user in line on waitlist: {}", user.getEmail());
        }
    }
}
