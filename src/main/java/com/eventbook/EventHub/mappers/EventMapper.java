package com.eventbook.EventHub.mappers;

import com.eventbook.EventHub.DTOs.*;
import com.eventbook.EventHub.entity.Ticket;
import com.eventbook.EventHub.entity.TicketType;
import com.eventbook.EventHub.models.CreateEventRequest;
import com.eventbook.EventHub.models.CreateTicketTypeRequest;
import com.eventbook.EventHub.entity.Event;
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
}
