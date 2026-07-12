package com.eventbook.EventHub.mappers;

import com.eventbook.EventHub.domain.DTOs.GetTicketResponseDto;
import com.eventbook.EventHub.domain.DTOs.ListTicketResponseDto;
import com.eventbook.EventHub.domain.DTOs.ListTicketTicketTypeResponseDto;
import com.eventbook.EventHub.domain.entity.Ticket;
import com.eventbook.EventHub.domain.entity.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface TicketMapper {

    ListTicketTicketTypeResponseDto toListTicketTypeResponseDto(TicketType ticketType);

    ListTicketResponseDto toListTicketResponseDto(Ticket ticket);

    @Mapping(target = "price" , source = "ticket.ticketType.price")
    @Mapping(target = "description" , source = "ticket.ticketType.description")
    @Mapping(target = "eventName" , source = "ticket.ticketType.event.name")
    @Mapping(target = "eventVenue" , source = "ticket.ticketType.event.venue")
    @Mapping(target = "eventStart" , source = "ticket.ticketType.event.start")
    @Mapping(target = "eventEnd" , source = "ticket.ticketType.event.end")
    GetTicketResponseDto toGetTicketResponseDto(Ticket ticket);


}
