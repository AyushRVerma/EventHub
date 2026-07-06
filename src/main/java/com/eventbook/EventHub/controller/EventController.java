package com.eventbook.EventHub.controller;

import com.eventbook.EventHub.DTOs.CreateEventRequestDto;
import com.eventbook.EventHub.DTOs.CreateEventResponseDto;
import com.eventbook.EventHub.DTOs.GetEventDetailsResponseDto;
import com.eventbook.EventHub.DTOs.ListEventResponseDto;
import com.eventbook.EventHub.entity.Event;
import com.eventbook.EventHub.mappers.EventMapper;
import com.eventbook.EventHub.models.CreateEventRequest;
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

    private UUID parseUserId(Jwt jwt){
        return UUID.fromString(jwt.getSubject());
    }


}
