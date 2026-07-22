package com.eventbook.EventHub.domain.DTOs;


import com.eventbook.EventHub.domain.entity.EventStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request body payload for creating a new event")
public class CreateEventRequestDto {

    @NotBlank(message = "Event name is required")
    @Schema(description = "Name of the event", example = "DIVINE Live Tour Delhi", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "Start date is required")
    @Future(message = "Event start date must be in the future")
    @Schema(description = "Date and time when the event starts", example = "2026-12-05T18:00:00")
    private LocalDateTime start;


    @NotNull(message = "End date is required")
    @Future(message = "Event end date must be in the future")
    @Schema(description = "Date and time when the event ends", example = "2026-12-05T23:30:00")
    private LocalDateTime end;

    @NotBlank(message = " Venue details is required")
    @Schema(description = "Location venue of the event", example = "DLF Avenue Auditorium, Saket", requiredMode = Schema.RequiredMode.REQUIRED)
    private String venue;

    @Schema(description = "Date when ticket sales begin", example = "2026-10-01T00:00:00")
    private LocalDateTime salesStart;

    @Schema(description = "Date when ticket sales close", example = "2026-12-04T23:59:59")
    private LocalDateTime salesEnd;

    @NotNull(message = "Event status must be provided")
    @Schema(description = "Initial status of the event (e.g., DRAFT, PUBLISHED)", example = "PUBLISHED")
    private EventStatusEnum status;

    @NotEmpty(message = "At least one ticket type is required")
    @Valid
    @Schema(description = "List of ticket tiers available for this event")
    private List<CreateTicketTypeRequestDto> ticketTypes;

}
