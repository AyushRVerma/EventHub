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

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {
    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;
    private final EmailService  emailService;

    @Override
    @Transactional
    public Ticket purchaseTicket(UUID userId, UUID ticketTypeId, UUID eventId) {
       User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
               String.format("User with id %s not found", userId))
       );
       TicketType ticketType= ticketTypeRepository.findById(ticketTypeId).orElseThrow(() -> new
               TicketTypeNotFoundException(String.format("Ticket type with id %s not found", ticketTypeId)));



       int purchasedTickets = ticketRepository.countByTicketTypeId(ticketType.getId());
       Integer totalAvailable = ticketType.getTotalAvailable();

       if(purchasedTickets + 1 > totalAvailable) {
           throw new TicketSoldOutException();
       }

       Ticket ticket = new Ticket();
       ticket.setStatus(TicketStatusEnum.PURCHASED);
       ticket.setTicketType(ticketType);
       ticket.setPurchaser(user);

       Ticket savedTicket= ticketRepository.save(ticket);
       qrCodeService.generateQrCode(savedTicket);

       Ticket finalTicket =  ticketRepository.save(savedTicket);

        String eventName  = ticketType.getEvent().getName();
        String venue      = ticketType.getEvent().getVenue();
        String eventStart = ticketType.getEvent().getStart().toString();
        String ticketTypeName = ticketType.getName();
        Double price      = ticketType.getPrice();
        String ticketId   = finalTicket.getId().toString();
        String userEmail  = user.getEmail();

       emailService.sendTicketConfirmationEmail(eventName, venue, eventStart, ticketTypeName, price, ticketId, userEmail);

       return finalTicket;
    }
}
