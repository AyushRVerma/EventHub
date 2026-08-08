package com.eventbook.EventHub.messaging;

import com.eventbook.EventHub.Config.RabbitMQConfig;
import com.eventbook.EventHub.domain.events.TicketPurchasedEvent;
import com.eventbook.EventHub.domain.events.TicketReleasedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendTicketReleasedEvent(UUID ticketTypeId) {
        TicketReleasedEvent event = new TicketReleasedEvent(ticketTypeId);
        log.info("Publishing TicketReleasedEvent to RabbitMQ for ticketTypeId: {}", ticketTypeId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_TICKET_RELEASED,
                event
        );
    }

    public void sendTicketPurchasedEvent(TicketPurchasedEvent event) {
        log.info("Publishing TicketPurchasedEvent to RabbitMQ for recipient: {}", event.getRecipientEmail());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_TICKET_PURCHASED,
                event
        );
    }
}
