package com.eventbook.EventHub.services.impl;

import com.eventbook.EventHub.domain.DTOs.GetEventDetailsResponseDto;
import com.eventbook.EventHub.domain.DTOs.GetPublishedEventDetailsResponseDto;
import com.eventbook.EventHub.domain.DTOs.ListPublishedEventResponseDto;
import com.eventbook.EventHub.domain.entity.EventStatusEnum;
import com.eventbook.EventHub.domain.models.CreateEventRequest;
import com.eventbook.EventHub.domain.entity.Event;
import com.eventbook.EventHub.domain.entity.TicketType;
import com.eventbook.EventHub.domain.entity.User;
import com.eventbook.EventHub.domain.models.UpdateEventRequest;
import com.eventbook.EventHub.domain.models.UpdateTicketTypeRequest;
import com.eventbook.EventHub.exceptions.*;
import com.eventbook.EventHub.mappers.EventMapper;
import com.eventbook.EventHub.repositories.EventRepository;
import com.eventbook.EventHub.repositories.UserRepository;
import com.eventbook.EventHub.services.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventMapper  eventMapper;

    @Override
    @CacheEvict(value = {"published_events", "published_event_details"}, allEntries=true)
    public Event createdEvent(UUID organizerId, CreateEventRequest event) {
       User organizer= userRepository.findById(organizerId)
               .orElseThrow(() -> new UserNotFoundException(
                       String.format("User with id %s not found", organizerId))
               );

        if(event.getStart()!=null && event.getEnd()!=null){
            if(event.getStart().isAfter(event.getEnd())){
                throw new InvalidEventDatesException("Event end date must be after the start date");
            }
        }

        if(event.getSalesStart()!=null && event.getSalesEnd()!=null){
            if(event.getSalesEnd().isAfter(event.getSalesEnd())){
                throw new InvalidEventDatesException("Event sales end date must be after the Event sales start date");
            }
        }

        if(event.getSalesStart()!=null && event.getSalesEnd()!=null && event.getStart()!=null && event.getEnd()!=null){
            if(event.getSalesStart().isAfter(event.getStart()) && event.getSalesEnd().isAfter(event.getStart())){
                throw new InvalidEventDatesException("Event start date must be after the Event sales duration date");
            }
        }


        Event eventToCreate = new Event();

       List<TicketType> ticketTypesToCreate = event.getTicketTypes().stream().map(
               ticketType -> {
                   TicketType ticketTypeToCreate = new TicketType();
                   ticketTypeToCreate.setName(ticketType.getName());
                   ticketTypeToCreate.setPrice(ticketType.getPrice());
                   ticketTypeToCreate.setDescription(ticketType.getDescription());
                   ticketTypeToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                   ticketTypeToCreate.setEvent(eventToCreate);
                   return ticketTypeToCreate;
               }).toList();


       eventToCreate.setName(event.getName());
       eventToCreate.setStart(event.getStart());
       eventToCreate.setEnd(event.getEnd());
       eventToCreate.setVenue(event.getVenue());
       eventToCreate.setSalesStart(event.getSalesStart());
       eventToCreate.setSalesEnd(event.getSalesEnd());
       eventToCreate.setStatus(event.getStatus());
       eventToCreate.setOrganizer(organizer);
       eventToCreate.setTicketTypes(ticketTypesToCreate);

       return eventRepository.save(eventToCreate);

    }

    @Override
    public Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId, pageable);
    }

    @Override
    public Optional<Event> getEventForOrganizer(UUID organizerId, UUID id) {
        return eventRepository.findByIdAndOrganizerId(id, organizerId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"published_events", "published_event_details"}, allEntries=true)
    public Event updateEventForOrganizer(UUID organizerId, UUID id, UpdateEventRequest event) {
        if(event.getId()==null){
            throw new EventUpdateException("Event Id cannot be null");
        }

        if(!id.equals(event.getId())){
            throw new EventUpdateException("Provided id does not match");

        }

        if(event.getStart()!=null && event.getEnd()!=null){
            if(event.getStart().isAfter(event.getEnd())){
                throw new InvalidEventDatesException("Event end date must be after the start date");
            }
        }

        if(event.getSalesStart()!=null && event.getSalesEnd()!=null){
            if(event.getSalesEnd().isAfter(event.getSalesEnd())){
                throw new InvalidEventDatesException("Event sales end date must be after the Event sales start date");
            }
        }

        if(event.getSalesStart()!=null && event.getSalesEnd()!=null && event.getStart()!=null && event.getEnd()!=null){
            if(event.getSalesStart().isAfter(event.getStart()) && event.getSalesEnd().isAfter(event.getStart())){
                throw new InvalidEventDatesException("Event start date must be after the Event sales duration date");
            }
        }

        Event existingEvent = eventRepository.findByIdAndOrganizerId(id, organizerId)
        .orElseThrow(() -> new EventNotFoundException(
                String.format("Event with id %s not found", id)));

        existingEvent.setName(event.getName());
        existingEvent.setStart(event.getStart());
        existingEvent.setEnd(event.getEnd());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setSalesStart(event.getSalesStart());
        existingEvent.setSalesEnd(event.getSalesEnd());
        existingEvent.setStatus(event.getStatus());


        Set<UUID> requestTicketTypeIds= event.getTicketTypes()
                .stream()
                .map(UpdateTicketTypeRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());


        existingEvent.getTicketTypes().removeIf(
                existingTicketType -> !requestTicketTypeIds
                                                .contains(existingTicketType.getId()));

        Map<UUID, TicketType> existingTicketTypesIndex=existingEvent.getTicketTypes().stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));

        for(UpdateTicketTypeRequest ticketType :  event.getTicketTypes()){
            if(ticketType.getId()==null){
                 //create
                TicketType ticketTypeToCreate = new TicketType();
                ticketTypeToCreate.setName(ticketType.getName());
                ticketTypeToCreate.setPrice(ticketType.getPrice());
                ticketTypeToCreate.setDescription(ticketType.getDescription());
                ticketTypeToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                ticketTypeToCreate.setEvent(existingEvent);
                existingEvent.getTicketTypes().add(ticketTypeToCreate);

            }
            else if(existingTicketTypesIndex.containsKey(ticketType.getId())){
                 //update
                 TicketType existingTicketType = existingTicketTypesIndex.get(ticketType.getId());

                 existingTicketType.setName(ticketType.getName());
                 existingTicketType.setPrice(ticketType.getPrice());
                 existingTicketType.setDescription(ticketType.getDescription());
                 existingTicketType.setTotalAvailable(ticketType.getTotalAvailable());

            }

            else{
                throw new TicketTypeNotFoundException(String.format("Ticket Type with id %s not found", ticketType.getId()));
            }


        }

        return eventRepository.save(existingEvent);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"published_events", "published_event_details"}, allEntries = true)
    public void deleteEventForOrganizer(UUID organizerId, UUID id) {
        getEventForOrganizer(organizerId, id).ifPresent(eventRepository::delete);
    }

    @Override
//    @Cacheable(value="published_events", key="#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ListPublishedEventResponseDto> listPublishedEvents(Pageable pageable) {
        Page<Event> events= eventRepository.findByStatus(EventStatusEnum.PUBLISHED, pageable);
       return events.map(eventMapper::toListPublishedEventResponseDto);
    }

    @Override
    public Page<ListPublishedEventResponseDto> searchPublishedEvents(String query, Pageable pageable) {
          return eventRepository.searchEvents(query, pageable)
                  .map(eventMapper::toListPublishedEventResponseDto);
    }

    @Override
    @Cacheable(value = "published_event_details", key = "#id")
    public Optional<GetPublishedEventDetailsResponseDto> getPublishedEvents(UUID id) {
       return eventRepository.findByIdAndStatus(id, EventStatusEnum.PUBLISHED)
               .map(eventMapper::toGetPublishedEventDetailsResponseDto);
    }


}
