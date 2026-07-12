package com.eventbook.EventHub.mappers;


import com.eventbook.EventHub.domain.DTOs.TicketValidationResponseDto;
import com.eventbook.EventHub.domain.entity.TicketValidation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface TicketValidationMapper {

    @Mapping(target="ticketId" , source = "ticket.id")
    TicketValidationResponseDto toTicketValidationResponseDto(TicketValidation ticketValidation);


}
