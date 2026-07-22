package com.eventbook.EventHub.controller;


import com.eventbook.EventHub.domain.DTOs.GetPublishedEventDetailsResponseDto;
import com.eventbook.EventHub.domain.DTOs.ListPublishedEventResponseDto;
import com.eventbook.EventHub.domain.entity.Event;
import com.eventbook.EventHub.mappers.EventMapper;
import com.eventbook.EventHub.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/published-events")
@RequiredArgsConstructor
public class PublishedEventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping
    public ResponseEntity<Page<ListPublishedEventResponseDto>> listPublishedEvents(
            @RequestParam(required = false) String q, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {

        Page<ListPublishedEventResponseDto> events;
        if(null!=q && !q.trim().isEmpty()){
            events =  eventService.searchPublishedEvents(q, pageable);
        }

        else{
            events= eventService.listPublishedEvents(pageable);
        }

        return ResponseEntity.ok(events);
//        return ResponseEntity.ok(eventService.listPublishedEvents(pageable)
//                .map(eventMapper::toListPublishedEventResponseDto));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<GetPublishedEventDetailsResponseDto> getPublishedEventDetails(
            @PathVariable UUID eventId
    ){
      return  eventService.getPublishedEvents(eventId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
