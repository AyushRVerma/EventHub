package com.eventbook.EventHub.domain.DTOs;


import com.eventbook.EventHub.domain.entity.EventStatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class    CreateEventRequestDto {

    @NotBlank(message = "Event name is required")
    private String name;

    @NotNull(message = "Start date is required")
    @Future(message = "Event start date must be in the future")
    private LocalDateTime start;


    @NotNull(message = "End date is required")
    @Future(message = "Event end date must be in the future")
    private LocalDateTime end;

    @NotBlank(message = " Venue details is required")
    private String venue;

    private LocalDateTime salesStart;

    private LocalDateTime salesEnd;

    @NotNull(message = "Event status must be provided")
    private EventStatusEnum status;

    @NotEmpty(message = "At least one ticket type is required")
    @Valid
    private List<CreateTicketTypeRequestDto> ticketTypes;

}
