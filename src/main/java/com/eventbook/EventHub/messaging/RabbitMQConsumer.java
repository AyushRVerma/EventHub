package com.eventbook.EventHub.messaging;

import com.eventbook.EventHub.Config.RabbitMQConfig;
import com.eventbook.EventHub.domain.events.TicketPurchasedEvent;
import com.eventbook.EventHub.domain.events.TicketReleasedEvent;
import com.eventbook.EventHub.services.WaitlistService;
import com.eventbook.EventHub.services.impl.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final WaitlistService waitlistService;
    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.WAITLIST_QUEUE)
    public void consumeTicketReleasedEvent(TicketReleasedEvent event) {
        log.info("Received TicketReleasedEvent from RabbitMQ: {}", event.getTicketTypeId());
        try {
            waitlistService.processWaitlistOnTicketRelease(event.getTicketTypeId());
        } catch (Exception e) {
            log.error("Error processing waitlist from RabbitMQ consumer: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_CONFIRMATION_QUEUE)
    public void consumeTicketPurchasedEvent(TicketPurchasedEvent event) {
        log.info("Received TicketPurchasedEvent from RabbitMQ for email: {}", event.getRecipientEmail());
        try {
            emailService.sendTicketConfirmationEmail(
                    event.getEventName(),
                    event.getVenue(),
                    event.getEventStart(),
                    event.getTicketTypeName(),
                    event.getPrice(),
                    event.getTicketId(),
                    event.getRecipientEmail()
            );
        } catch (Exception e) {
            log.error("Error processing ticket confirmation email from RabbitMQ consumer: {}", e.getMessage());
        }
    }
}
