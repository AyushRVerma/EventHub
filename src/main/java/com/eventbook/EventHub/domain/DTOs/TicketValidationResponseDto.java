package com.eventbook.EventHub.domain.DTOs;


import com.eventbook.EventHub.domain.entity.TicketValidationMethod;
import com.eventbook.EventHub.domain.entity.TicketValidationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TicketValidationResponseDto {

    private UUID ticketId;
    private TicketValidationStatusEnum status;
}
