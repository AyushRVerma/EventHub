package com.eventbook.EventHub.repositories;

import com.eventbook.EventHub.domain.entity.WaitlistEntry;
import com.eventbook.EventHub.domain.entity.WaitlistStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WaitlistRepository extends JpaRepository<WaitlistEntry, UUID> {

    boolean existsByTicketTypeIdAndUserIdAndStatus(UUID ticketTypeId, UUID userId, WaitlistStatusEnum status);

    List<WaitlistEntry> findByTicketTypeIdAndStatusOrderByCreatedAtAsc(UUID ticketTypeId, WaitlistStatusEnum status);

    Optional<WaitlistEntry> findFirstByTicketTypeIdAndStatusOrderByCreatedAtAsc(UUID ticketTypeId, WaitlistStatusEnum status);

    int countByTicketTypeIdAndStatus(UUID ticketTypeId, WaitlistStatusEnum status);

    long countByTicketTypeEventOrganizerIdAndStatus(UUID organizerId, WaitlistStatusEnum status);
}
