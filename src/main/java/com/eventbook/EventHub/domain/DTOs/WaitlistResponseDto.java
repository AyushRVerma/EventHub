package com.eventbook.EventHub.domain.DTOs;

import com.eventbook.EventHub.domain.entity.WaitlistStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaitlistResponseDto {
    private UUID waitlistId;
    private UUID ticketTypeId;
    private String ticketTypeName;
    private String eventName;
    private Integer positionInQueue;
    private WaitlistStatusEnum status;
    private LocalDateTime joinedAt;
}
