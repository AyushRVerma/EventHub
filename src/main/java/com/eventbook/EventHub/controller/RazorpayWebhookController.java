package com.eventbook.EventHub.controller;

import com.eventbook.EventHub.domain.entity.Ticket;
import com.eventbook.EventHub.domain.entity.TicketStatusEnum;
import com.eventbook.EventHub.domain.entity.TicketType;
import com.eventbook.EventHub.domain.entity.User;
import com.eventbook.EventHub.repositories.TicketRepository;
import com.eventbook.EventHub.services.QrCodeService;
import com.eventbook.EventHub.services.RazorpayService;
import com.eventbook.EventHub.domain.events.TicketPurchasedEvent;
import com.eventbook.EventHub.messaging.RabbitMQProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/webhooks/razorpay")
@RequiredArgsConstructor
@Slf4j
public class RazorpayWebhookController {

    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;
    private final RazorpayService razorpayService;
    private final RabbitMQProducer rabbitMQProducer;

    @PostMapping
    public ResponseEntity<String> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        // 1. Verify HMAC Signature
        boolean isValid = razorpayService.verifyWebhookSignature(payload, signature);
        if (!isValid) {
            log.error("Invalid Razorpay Webhook signature!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Signature");
        }

        JSONObject json = new JSONObject(payload);
        String event = json.optString("event");

        log.info("Received Razorpay Webhook Event: {}", event);

        // 2. Handle Payment Captured or Order Paid
        if ("payment.captured".equals(event) || "order.paid".equals(event)) {
            JSONObject payloadObj = json.getJSONObject("payload");
            JSONObject paymentObj = payloadObj.getJSONObject("payment").getJSONObject("entity");

            String orderId = paymentObj.optString("order_id");
            String paymentId = paymentObj.optString("id");

            JSONObject notes = paymentObj.optJSONObject("notes");
            String ticketIdStr = notes != null ? notes.optString("ticketId") : null;

            Ticket ticketToUpdate = null;

            if (ticketIdStr != null && !ticketIdStr.isEmpty()) {
                ticketToUpdate = ticketRepository.findById(UUID.fromString(ticketIdStr)).orElse(null);
            } else if (orderId != null) {
                ticketToUpdate = ticketRepository.findByRazorpayOrderId(orderId).orElse(null);
            }

            if (ticketToUpdate != null && ticketToUpdate.getStatus() == TicketStatusEnum.PENDING_PAYMENT) {
                // Update status to PURCHASED
                ticketToUpdate.setStatus(TicketStatusEnum.PURCHASED);
                ticketToUpdate.setRazorpayPaymentId(paymentId);
                Ticket savedTicket = ticketRepository.save(ticketToUpdate);

                // Generate QR Code
                qrCodeService.generateQrCode(savedTicket);

                // Publish TicketPurchasedEvent to RabbitMQ!
                TicketType ticketType = savedTicket.getTicketType();
                User user = savedTicket.getPurchaser();
                if (ticketType != null && user != null) {
                    TicketPurchasedEvent emailEvent = TicketPurchasedEvent.builder()
                            .eventName(ticketType.getEvent().getName())
                            .venue(ticketType.getEvent().getVenue())
                            .eventStart(ticketType.getEvent().getStart().toString())
                            .ticketTypeName(ticketType.getName())
                            .price(ticketType.getPrice())
                            .ticketId(savedTicket.getId().toString())
                            .recipientEmail(user.getEmail())
                            .build();

                    rabbitMQProducer.sendTicketPurchasedEvent(emailEvent);
                }
                log.info("Ticket {} marked as PURCHASED. Published email event to RabbitMQ.", savedTicket.getId());
            }
        } else if ("payment.failed".equals(event) || "refund.processed".equals(event)) {
            JSONObject payloadObj = json.getJSONObject("payload");
            JSONObject paymentObj = payloadObj.getJSONObject("payment").getJSONObject("entity");

            String orderId = paymentObj.optString("order_id");
            if (orderId != null) {
                ticketRepository.findByRazorpayOrderId(orderId).ifPresent(ticket -> {
                    if (ticket.getStatus() == TicketStatusEnum.PENDING_PAYMENT) {
                        ticket.setStatus(TicketStatusEnum.EXPIRED);
                        ticketRepository.save(ticket);
                        log.info("Ticket reservation {} EXPIRED due to payment failure.", ticket.getId());

                        // Publish TicketReleasedEvent to RabbitMQ to process waitlist!
                        rabbitMQProducer.sendTicketReleasedEvent(ticket.getTicketType().getId());
                    }
                });
            }
        }

        return ResponseEntity.ok("OK");
    }
}
