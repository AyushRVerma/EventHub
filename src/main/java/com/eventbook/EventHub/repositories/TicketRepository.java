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
}
