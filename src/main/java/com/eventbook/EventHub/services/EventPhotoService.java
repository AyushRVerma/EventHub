package com.eventbook.EventHub.services;

import com.eventbook.EventHub.domain.DTOs.EventPhotoResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface EventPhotoService {

    EventPhotoResponseDto uploadPhoto(UUID eventId, UUID userId, MultipartFile file);

    List<EventPhotoResponseDto> getPhotosForEvent(UUID eventId);

    byte[] getPhotoBytes(UUID photoId);

    String getPhotoContentType(UUID photoId);
}
