package com.eventbook.EventHub.domain.DTOs;

import com.eventbook.EventHub.domain.entity.TicketStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListTicketResponseDto {

    private UUID id;
    private TicketStatusEnum status;
    private ListTicketTicketTypeResponseDto ticketType;
}
