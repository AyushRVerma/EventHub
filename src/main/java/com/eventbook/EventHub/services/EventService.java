package com.eventbook.EventHub.services;

import com.eventbook.EventHub.models.CreateEventRequest;
import com.eventbook.EventHub.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.awt.font.OpenType;
import java.util.Optional;
import java.util.UUID;

public interface EventService {
    Event createdEvent(UUID organizerId, CreateEventRequest event);

    Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable);
    Optional<Event> getEventForOrganizer(UUID organizerId, UUID id);
}
