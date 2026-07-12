package com.eventbook.EventHub.repositories;

import com.eventbook.EventHub.domain.entity.TicketValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketValidationRespository extends JpaRepository<TicketValidation, UUID> {
}
