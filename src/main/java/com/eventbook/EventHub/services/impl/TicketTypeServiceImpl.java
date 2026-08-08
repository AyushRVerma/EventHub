package com.eventbook.EventHub.services.impl;

import com.eventbook.EventHub.domain.entity.TicketStatusEnum;
import com.eventbook.EventHub.domain.entity.Ticket;
import com.eventbook.EventHub.domain.entity.TicketType;
import com.eventbook.EventHub.domain.entity.User;
import com.eventbook.EventHub.exceptions.TicketSoldOutException;
import com.eventbook.EventHub.exceptions.TicketTypeNotFoundException;
import com.eventbook.EventHub.exceptions.UserNotFoundException;
import com.eventbook.EventHub.repositories.TicketRepository;
import com.eventbook.EventHub.repositories.TicketTypeRepository;
import com.eventbook.EventHub.repositories.UserRepository;
import com.eventbook.EventHub.services.QrCodeService;
import com.eventbook.EventHub.services.TicketTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import com.eventbook.EventHub.domain.DTOs.CreateRazorpayOrderResponseDto;
import com.eventbook.EventHub.services.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {
    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final RazorpayService razorpayService;

    @Override
    @Transactional
    public CreateRazorpayOrderResponseDto purchaseTicket(UUID userId, UUID ticketTypeId, UUID eventId) {
       User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
               String.format("User with id %s not found", userId))
       );
       TicketType ticketType = ticketTypeRepository.findById(ticketTypeId).orElseThrow(() -> new
               TicketTypeNotFoundException(String.format("Ticket type with id %s not found", ticketTypeId)));

       int purchasedTickets = ticketRepository.countByTicketTypeId(ticketType.getId());
       Integer totalAvailable = ticketType.getTotalAvailable();

       if(purchasedTickets + 1 > totalAvailable) {
           throw new TicketSoldOutException();
       }

       // Create ticket in PENDING_PAYMENT state (Lock Reservation)
       Ticket ticket = new Ticket();
       ticket.setStatus(TicketStatusEnum.PENDING_PAYMENT);
       ticket.setTicketType(ticketType);
       ticket.setPurchaser(user);

       Ticket savedTicket = ticketRepository.save(ticket);

       try {
           // Create Razorpay Order
           Order order = razorpayService.createOrder(savedTicket, ticketType);
           String orderId = order.get("id");
           long amount = ((Number) order.get("amount")).longValue();
           String currency = order.get("currency");

           savedTicket.setRazorpayOrderId(orderId);
           ticketRepository.save(savedTicket);

           return CreateRazorpayOrderResponseDto.builder()
                   .ticketId(savedTicket.getId())
                   .orderId(orderId)
                   .amount(amount)
                   .currency(currency)
                   .keyId(razorpayService.getKeyId())
                   .build();
       } catch (RazorpayException e) {
           throw new RuntimeException("Failed to create Razorpay payment order", e);
       }
    }
}
