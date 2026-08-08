package com.eventbook.EventHub.domain.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRazorpayOrderResponseDto {
    private UUID ticketId;
    private String orderId;
    private Long amount; // in paise
    private String currency; // "INR"
    private String keyId; // Razorpay Key ID for frontend SDK
}
