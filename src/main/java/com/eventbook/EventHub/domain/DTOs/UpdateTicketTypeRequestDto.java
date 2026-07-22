package com.eventbook.EventHub.domain.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UpdateTicketTypeRequestDto {

    @Schema(description = "The unique identifier (UUID) of the ticket type. Leave null if adding a brand new ticket tier.", example = "e578c772-9b2f-4c55-a0a1-77ffc28ea9de")
    private UUID id;

    @NotBlank(message = "Ticket type is required ")
    @Schema(description = "Name of the ticket category", example = "Gully VIP Arena", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message="Price is required")
    @PositiveOrZero(message = "Price must be zero or greater")
    @Schema(description = "Ticket price", example = "2199.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    @Schema(description = "Description of the ticket inclusions", example = "Elevated seating area + private bar access + food voucher")
    private String description;
    @Schema(description = "Total capacity of tickets to be made available", example = "350")
    private Integer totalAvailable;
}