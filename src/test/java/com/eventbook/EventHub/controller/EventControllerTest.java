package com.eventbook.EventHub.controller;


import com.eventbook.EventHub.domain.DTOs.GetEventDetailsResponseDto;
import com.eventbook.EventHub.domain.entity.Event;
import com.eventbook.EventHub.mappers.EventMapper;
import com.eventbook.EventHub.repositories.EventRepository;
import com.eventbook.EventHub.repositories.UserRepository;
import com.eventbook.EventHub.services.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)

public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private EventMapper eventMapper;

    @MockitoBean
    private UserRepository userRepository;

//    @InjectMocks
//    private EventController eventController;

    private UUID userId;
    private UUID eventId;
    private Event mockEvent;
    private GetEventDetailsResponseDto mockDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();

        mockEvent = new Event();
        mockEvent.setId(eventId);
        mockEvent.setName("DIVINE Concert");

        mockDto  = new GetEventDetailsResponseDto();
        mockDto.setId(eventId);
        mockDto.setName("DIVINE Concert");
    }

    @Test
    void getEvent_WhenEventExists_ShouldReturn200AndDetails() throws Exception
    {
        when(eventService.getEventForOrganizer(userId,eventId))
                .thenReturn(Optional.of(mockEvent));
        when(eventMapper.toGetEventDetailsResponseDto(mockEvent))
                .thenReturn(mockDto);

        mockMvc.perform(get("/api/v1/events/" + eventId)
                .with(jwt().jwt(builder -> builder.subject(userId.toString())))
                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventId.toString()))
                .andExpect(jsonPath("$.name").value("DIVINE Concert"));

        verify(eventService, times(1)).getEventForOrganizer(userId,eventId);
        verify(eventMapper, times(1)).toGetEventDetailsResponseDto(mockEvent);
    }
    @Test
    void getEvent_WhenEventDoesNotExist_ShouldReturn404() throws Exception {
        // 1. Stub the Service to return empty
        when(eventService.getEventForOrganizer(userId, eventId))
                .thenReturn(Optional.empty());
        // 2. Perform GET request with a Mock JWT Token
        mockMvc.perform(get("/api/v1/events/" + eventId)
                        .with(jwt().jwt(builder -> builder.subject(userId.toString())))
                        .contentType(MediaType.APPLICATION_JSON))
                // 3. Expect HTTP 404 Not Found
                .andExpect(status().isNotFound());
        verify(eventService, times(1)).getEventForOrganizer(userId, eventId);
        verifyNoInteractions(eventMapper); // Mapper should never be called on 404
    }


}
