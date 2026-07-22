package com.eventbook.EventHub.services;

import com.eventbook.EventHub.domain.DTOs.GetEventDetailsResponseDto;
import com.eventbook.EventHub.domain.DTOs.GetPublishedEventDetailsResponseDto;
import com.eventbook.EventHub.domain.DTOs.ListPublishedEventResponseDto;
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
    Page<ListPublishedEventResponseDto> listPublishedEvents(Pageable pageable);
    Page<ListPublishedEventResponseDto> searchPublishedEvents(String query,Pageable pageable);
    Optional<GetPublishedEventDetailsResponseDto> getPublishedEvents(UUID id);

}
