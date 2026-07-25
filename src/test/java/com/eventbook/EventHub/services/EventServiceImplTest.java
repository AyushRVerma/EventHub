package com.eventbook.EventHub.services;


import com.eventbook.EventHub.domain.DTOs.GetPublishedEventDetailsResponseDto;
import com.eventbook.EventHub.domain.entity.Event;
import com.eventbook.EventHub.domain.entity.EventStatusEnum;
import com.eventbook.EventHub.mappers.EventMapper;
import com.eventbook.EventHub.repositories.EventRepository;
import com.eventbook.EventHub.services.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Tells JUnit to use Mockito for mocks
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    private UUID eventId;
    private Event mockEvent;
    private GetPublishedEventDetailsResponseDto mockDto;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        mockEvent = new Event();
        mockEvent.setId(eventId);
        mockEvent.setName("TestEvent");
        mockEvent.setStatus(EventStatusEnum.PUBLISHED);

        mockDto = new GetPublishedEventDetailsResponseDto();
        mockDto.setId(eventId);
        mockDto.setName("TestEvent");
    }

    @Test
    void getPublishedEvents_WhenEventExistsAndIsPublished_ShouldReturnEvent(){

        when(eventRepository.findByIdAndStatus(eventId,EventStatusEnum.PUBLISHED))
                .thenReturn(Optional.of(mockEvent));

        when(eventMapper.toGetPublishedEventDetailsResponseDto(mockEvent))
                .thenReturn(mockDto);

        Optional<GetPublishedEventDetailsResponseDto> result = eventService.getPublishedEvents(eventId);

        assertTrue(result.isPresent());
        assertEquals("TestEvent", result.get().getName());
        assertEquals(eventId, result.get().getId());

        verify(eventRepository).findByIdAndStatus(eventId,EventStatusEnum.PUBLISHED);
        verify(eventMapper).toGetPublishedEventDetailsResponseDto(mockEvent);
    }

    @Test
    void getPublishedEvents_WhenEventDoesNotExist_ShouldReturnEmpty(){
        when(eventRepository.findByIdAndStatus(eventId,EventStatusEnum.PUBLISHED))
        .thenReturn(Optional.empty());

        Optional<GetPublishedEventDetailsResponseDto> result = eventService.getPublishedEvents(eventId);

        assertFalse(result.isPresent());

        verify(eventRepository,times(1)).findByIdAndStatus(eventId,EventStatusEnum.PUBLISHED);
        verify(eventMapper, never()).toGetPublishedEventDetailsResponseDto(any());
    }
}
