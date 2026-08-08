package com.eventbook.EventHub.services;

import com.eventbook.EventHub.domain.entity.Ticket;
import com.eventbook.EventHub.domain.entity.TicketType;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id:rzp_test_placeholder_key_id}")
    private String keyId;

    @Value("${razorpay.key.secret:placeholder_key_secret}")
    private String keySecret;

    @Value("${razorpay.webhook.secret:placeholder_webhook_secret}")
    private String webhookSecret;

    public Order createOrder(Ticket ticket, TicketType ticketType) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);

        // Convert price to paise (1 INR = 100 paise)
        long amountInPaise = Math.round(ticketType.getPrice() * 100);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        // Razorpay receipt length must be <= 40 characters (UUID is 36 chars)
        orderRequest.put("receipt", ticket.getId().toString());

        JSONObject notes = new JSONObject();
        notes.put("ticketId", ticket.getId().toString());
        notes.put("ticketTypeId", ticketType.getId().toString());
        notes.put("purchaserId", ticket.getPurchaser().getId().toString());
        orderRequest.put("notes", notes);

        return razorpayClient.orders.create(orderRequest);
    }

    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            return Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (RazorpayException e) {
            return false;
        }
    }

    public String getKeyId() {
        return keyId;
    }
}
