package com.eventbook.EventHub.services;

import com.eventbook.EventHub.domain.models.CreateEventRequest;
import com.eventbook.EventHub.domain.entity.Event;
import com.eventbook.EventHub.domain.models.UpdateEventRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface EventService {
    Event createdEvent(UUID organizerId, CreateEventRequest event);

    Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable);
    Optional<Event> getEventForOrganizer(UUID organizerId, UUID id);
    Event updateEventForOrganizer(UUID organizerId, UUID id, UpdateEventRequest  event);
    void deleteEventForOrganizer(UUID organizerId, UUID id);
    Page<Event> listPublishedEvents(Pageable pageable);
    Page<Event> searchPublishedEvents(String query,Pageable pageable);
    Optional<Event> getPublishedEvents(UUID id);

}
