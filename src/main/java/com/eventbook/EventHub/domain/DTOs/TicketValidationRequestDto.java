package com.eventbook.EventHub.domain.DTOs;


import com.eventbook.EventHub.domain.entity.TicketValidationMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationRequestDto {

    private UUID id;
    private TicketValidationMethod method;

}
