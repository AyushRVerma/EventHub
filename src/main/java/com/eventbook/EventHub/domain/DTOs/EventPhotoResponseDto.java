package com.eventbook.EventHub.domain.DTOs;

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
public class EventPhotoResponseDto {
    private UUID photoId;
    private UUID eventId;
    private String uploaderName;
    private String fileName;
    private String photoUrl;
    private LocalDateTime uploadedAt;
}
