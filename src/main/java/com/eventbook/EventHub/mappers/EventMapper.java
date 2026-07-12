package com.eventbook.EventHub.mappers;

import com.eventbook.EventHub.domain.DTOs.*;
import com.eventbook.EventHub.domain.entity.TicketType;
import com.eventbook.EventHub.domain.models.CreateEventRequest;
import com.eventbook.EventHub.domain.models.CreateTicketTypeRequest;
import com.eventbook.EventHub.domain.entity.Event;
import com.eventbook.EventHub.domain.models.UpdateEventRequest;
import com.eventbook.EventHub.domain.models.UpdateTicketTypeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper  {

    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);


    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);

    ListEventTicketTypeResponseDto toDto(TicketType ticketType);

    ListEventResponseDto toListEventResponseDto(Event event);

    GetEventDetailsTicketTypesResponseDto toGetEventDetailsTicketTypesResponseDto (TicketType ticketType);

    GetEventDetailsResponseDto toGetEventDetailsResponseDto(Event event);

    UpdateTicketTypeRequest fromDto(UpdateTicketTypeRequestDto dto);

    UpdateEventRequest fromDto(UpdateEventRequestDto dto);

    UpdateTicketTypeResponseDto toUpdateTicketTypeResponseDto(TicketType ticketType);

    UpdateEventResponseDto toUpdateEventResponseDto(Event event);

    ListPublishedEventResponseDto toListPublishedEventResponseDto(Event event);

   GetPublishedEventDetailsResponseDto toGetPublishedEventDetailsResponseDto(Event event);

   GetPublishedEventDetailsTicketTypesResponseDto toGetPublishedEventDetailsTicketTypesResponseDto(TicketType ticketType);


}
