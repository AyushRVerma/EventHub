package com.eventbook.EventHub.domain.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body payload for defining a ticket category")
public class CreateTicketTypeRequestDto {

    @NotBlank(message = "Ticket type is required ")
    @Schema(description = "Name of the ticket category", example = "VIP Moshpit Pass", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message="Price is required")
    @PositiveOrZero(message = "Price must be zero or greater")
    @Schema(description = "Price of the ticket in local currency", example = "2499.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    @Schema(description = "Short description of what the ticket includes", example = "Front row access + official concert hoodie")
    private String description;

    @Schema(description = "Total number of tickets available in this category", example = "150", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalAvailable;
}