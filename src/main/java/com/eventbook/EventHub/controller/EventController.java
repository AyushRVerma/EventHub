package com.eventbook.EventHub.controller;

import com.eventbook.EventHub.domain.DTOs.*;
import com.eventbook.EventHub.domain.entity.Event;
import com.eventbook.EventHub.domain.models.UpdateEventRequest;
import com.eventbook.EventHub.mappers.EventMapper;
import com.eventbook.EventHub.domain.models.CreateEventRequest;
import com.eventbook.EventHub.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

import static com.eventbook.EventHub.util.JwtUtil.parseUserId;

@RestController
@RequestMapping(path = "/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping
    public ResponseEntity<CreateEventResponseDto> createEvent(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateEventRequestDto createEventRequestDto)
    {
       CreateEventRequest createEventRequest= eventMapper.fromDto(createEventRequestDto);
        UUID userId = UUID.fromString(jwt.getSubject());

        Event createdEvent = eventService.createdEvent(userId,createEventRequest);
        CreateEventResponseDto createEventResponseDto= eventMapper.toDto(createdEvent);
        return new ResponseEntity<>(createEventResponseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<UpdateEventResponseDto> updateEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventRequestDto updateEventRequestDto)
    {
        UpdateEventRequest updateEventRequest= eventMapper.fromDto(updateEventRequestDto);
        UUID userId = parseUserId(jwt);

        Event updatedEvent = eventService.updateEventForOrganizer(userId,eventId,updateEventRequest);

        UpdateEventResponseDto updateEventResponseDto= eventMapper.toUpdateEventResponseDto(updatedEvent);
        return ResponseEntity.ok(updateEventResponseDto);
    }


    @GetMapping
    public ResponseEntity<Page<ListEventResponseDto>> listEvents(
            @AuthenticationPrincipal Jwt jwt, Pageable pageable
    ) {
        UUID userId = parseUserId(jwt);
        Page<Event> events = eventService.listEventsForOrganizer(userId,pageable);

        return ResponseEntity.ok(
                events.map(eventMapper::toListEventResponseDto));
    }

    @GetMapping(path= "/{eventId}")
    public ResponseEntity<GetEventDetailsResponseDto> getEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID eventId
    ){
            UUID userId = parseUserId(jwt);
            return eventService.getEventForOrganizer(userId,eventId)
                    .map(eventMapper::toGetEventDetailsResponseDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@AuthenticationPrincipal
                                            Jwt jwt, @PathVariable UUID eventId) {
        UUID userId = parseUserId(jwt);
        eventService.deleteEventForOrganizer(userId,eventId);
        return ResponseEntity.noContent().build();
    }




}
