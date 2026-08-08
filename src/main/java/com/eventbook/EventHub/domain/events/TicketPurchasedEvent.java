package com.eventbook.EventHub.domain.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketPurchasedEvent implements Serializable {
    private String eventName;
    private String venue;
    private String eventStart;
    private String ticketTypeName;
    private Double price;
    private String ticketId;
    private String recipientEmail;
}
