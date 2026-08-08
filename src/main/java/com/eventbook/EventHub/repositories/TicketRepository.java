package com.eventbook.EventHub.repositories;

import com.eventbook.EventHub.domain.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    int countByTicketTypeId(UUID ticketTypeId);

    Page<Ticket> findByPurchaserId(UUID purchaserId, Pageable pageable);

    Optional<Ticket> findByIdAndPurchaserId(UUID id, UUID purchaserId);

    Optional<Ticket> findByRazorpayOrderId(String razorpayOrderId);

    long countByTicketTypeEventOrganizerIdAndStatus(UUID organizerId, com.eventbook.EventHub.domain.entity.TicketStatusEnum status);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(t.ticketType.price), 0.0) FROM Ticket t WHERE t.ticketType.event.organizer.id = :organizerId AND t.status = com.eventbook.EventHub.domain.entity.TicketStatusEnum.PURCHASED")
    Double calculateTotalRevenueForOrganizer(@org.springframework.data.repository.query.Param("organizerId") UUID organizerId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(t) FROM Ticket t WHERE t.ticketType.event.organizer.id = :organizerId AND t.status = com.eventbook.EventHub.domain.entity.TicketStatusEnum.PURCHASED AND t.validation IS NOT EMPTY")
    Long countValidatedTicketsForOrganizer(@org.springframework.data.repository.query.Param("organizerId") UUID organizerId);
}
