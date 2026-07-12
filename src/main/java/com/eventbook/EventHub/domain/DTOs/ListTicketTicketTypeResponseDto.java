package com.eventbook.EventHub.domain.DTOs;

import com.eventbook.EventHub.domain.entity.TicketStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListTicketTicketTypeResponseDto {

    private UUID id;
    private String name;
    private Double price;
}
