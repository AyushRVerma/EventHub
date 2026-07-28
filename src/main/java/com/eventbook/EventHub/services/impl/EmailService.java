package com.eventbook.EventHub.services.impl;

import com.eventbook.EventHub.domain.entity.Ticket;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendTicketConfirmationEmail(
            String eventName,
            String venue,
            String eventStart,
            String ticketTypeName,
            Double price,
            String ticketId,
            String recipientEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("🎟️ Ticket Confirmed - " + eventName);
            helper.setText(buildEmailBody(
                    eventName, venue, eventStart, ticketTypeName, price, ticketId
            ), true);

            mailSender.send(message);
            log.info("✅ Email sent to: {}", recipientEmail);

        } catch (MessagingException e) {
            log.error("❌ Email failed: {}", e.getMessage());
        }
    }

    private String buildEmailBody(String eventName, String venue, String eventStart,
                                  String ticketTypeName, Double price, String ticketId) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                    <div style="max-width: 600px; margin: auto; background: white;
                                border-radius: 10px; padding: 30px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                        <h1 style="color: #6c63ff; text-align: center;">🎟️ Booking Confirmed!</h1>
                        <hr style="border: 1px solid #eee;">
                        <h2 style="color: #333;">%s</h2>
                        <table style="width: 100%%; border-collapse: collapse; margin-top: 20px;">
                            <tr style="background-color: #f9f9f9;">
                                <td style="padding: 10px; font-weight: bold;">📍 Venue</td>
                                <td style="padding: 10px;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 10px; font-weight: bold;">📅 Date & Time</td>
                                <td style="padding: 10px;">%s</td>
                            </tr>
                            <tr style="background-color: #f9f9f9;">
                                <td style="padding: 10px; font-weight: bold;">🎫 Ticket Type</td>
                                <td style="padding: 10px;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 10px; font-weight: bold;">💰 Price</td>
                                <td style="padding: 10px;">₹%.2f</td>
                            </tr>
                            <tr style="background-color: #f9f9f9;">
                                <td style="padding: 10px; font-weight: bold;">🆔 Ticket ID</td>
                                <td style="padding: 10px; font-size: 12px; color: #888;">%s</td>
                            </tr>
                        </table>
                        <div style="margin-top: 30px; padding: 15px; background: #6c63ff;
                                    border-radius: 8px; text-align: center;">
                            <p style="color: white; margin: 0;">
                                Show this email or your QR code at the venue entrance 🎉
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(eventName, venue, eventStart, ticketTypeName, price, ticketId);
    }
}