package com.eventbook.EventHub.domain.DTOs;


import com.eventbook.EventHub.domain.entity.EventStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body payload for updating an existing event details and ticket types")
public class UpdateEventRequestDto {

    @NotNull(message = "Event ID must be provided" )
    @Schema(description = "The unique identifier (UUID) of the event to be updated", example = "a29d3324-913a-4a2a-b08e-1284f1837bcf", requiredMode = Schema.RequiredMode.REQUIRED)
     private UUID id;

    @NotBlank(message = "Event name is required")
    @Schema(description = "Updated name of the event", example = "DIVINE - Punya Paap Tour Mumbai (Updated)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Updated start date and time of the event", example = "2026-11-20T19:30:00")
    private LocalDateTime start;

    @Schema(description = "Updated end date and time of the event", example = "2026-11-20T23:30:00")
    private LocalDateTime end;

    @NotBlank(message = " Venue details is required")
    @Schema(description = "Updated location/venue for the event", example = "NSCI Dome, Worli, Mumbai", requiredMode = Schema.RequiredMode.REQUIRED)
    private String venue;

    @Schema(description = "Updated start date for ticket sales", example = "2026-09-01T00:00:00")
    private LocalDateTime salesStart;

    @Schema(description = "Updated end date for ticket sales", example = "2026-11-19T23:59:59")
    private LocalDateTime salesEnd;

    @NotNull(message = "Event status must be provided")
    @Schema(description = "Updated event workflow status (e.g. DRAFT, PUBLISHED, CANCELLED)", example = "PUBLISHED", requiredMode = Schema.RequiredMode.REQUIRED)
    private EventStatusEnum status;

    @NotEmpty(message = "At least one ticket type is required")
    @Valid
    @Schema(description = "Updated list of ticket categories and capacities")
    private List<UpdateTicketTypeRequestDto> ticketTypes;

}
