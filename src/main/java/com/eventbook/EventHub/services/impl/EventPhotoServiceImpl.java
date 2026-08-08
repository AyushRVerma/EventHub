package com.eventbook.EventHub.services.impl;

import com.eventbook.EventHub.domain.DTOs.EventPhotoResponseDto;
import com.eventbook.EventHub.domain.entity.Event;
import com.eventbook.EventHub.domain.entity.EventPhoto;
import com.eventbook.EventHub.domain.entity.User;
import com.eventbook.EventHub.exceptions.EventNotFoundException;
import com.eventbook.EventHub.exceptions.UserNotFoundException;
import com.eventbook.EventHub.repositories.EventPhotoRepository;
import com.eventbook.EventHub.repositories.EventRepository;
import com.eventbook.EventHub.repositories.UserRepository;
import com.eventbook.EventHub.services.EventPhotoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPhotoServiceImpl implements EventPhotoService {

    private static final int MAX_PHOTOS_PER_USER_PER_EVENT = 5;
    private static final String UPLOAD_DIR = "uploads/photos/";

    private final EventPhotoRepository eventPhotoRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EventPhotoResponseDto uploadPhoto(UUID eventId, UUID userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files (JPEG, PNG, WEBP) are allowed");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(String.format("Event with id %s not found", eventId))
        );

        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("User with id %s not found", userId))
        );

        // Enforce max 5 photos per user per event limit!
        int userPhotoCount = eventPhotoRepository.countByEventIdAndUploaderId(eventId, userId);
        if (userPhotoCount >= MAX_PHOTOS_PER_USER_PER_EVENT) {
            throw new IllegalStateException("Maximum limit of " + MAX_PHOTOS_PER_USER_PER_EVENT + " photos per user reached for this event.");
        }

        try {
            // Ensure directory exists
            String eventDir = UPLOAD_DIR + eventId;
            File dir = new File(eventDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";

            String newFileName = UUID.randomUUID() + fileExtension;
            Path filePath = Paths.get(eventDir, newFileName);
            Files.write(filePath, file.getBytes());

            EventPhoto photo = EventPhoto.builder()
                    .event(event)
                    .uploader(user)
                    .fileName(originalFilename != null ? originalFilename : newFileName)
                    .fileType(contentType)
                    .storagePath(filePath.toString())
                    .build();

            EventPhoto savedPhoto = eventPhotoRepository.save(photo);

            log.info("User {} uploaded photo {} for event {} (Count: {}/5)",
                    user.getEmail(), savedPhoto.getId(), event.getName(), userPhotoCount + 1);

            return mapToDto(savedPhoto);

        } catch (IOException e) {
            log.error("Failed to save uploaded photo to disk: {}", e.getMessage());
            throw new RuntimeException("Could not store photo: " + e.getMessage());
        }
    }

    @Override
    public List<EventPhotoResponseDto> getPhotosForEvent(UUID eventId) {
        return eventPhotoRepository.findByEventIdOrderByUploadedAtDesc(eventId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] getPhotoBytes(UUID photoId) {
        EventPhoto photo = eventPhotoRepository.findById(photoId).orElseThrow(() ->
                new IllegalArgumentException("Photo not found with id: " + photoId)
        );
        try {
            return Files.readAllBytes(Paths.get(photo.getStoragePath()));
        } catch (IOException e) {
            throw new RuntimeException("Could not read photo file: " + e.getMessage());
        }
    }

    @Override
    public String getPhotoContentType(UUID photoId) {
        EventPhoto photo = eventPhotoRepository.findById(photoId).orElseThrow(() ->
                new IllegalArgumentException("Photo not found with id: " + photoId)
        );
        return photo.getFileType();
    }

    private EventPhotoResponseDto mapToDto(EventPhoto photo) {
        return EventPhotoResponseDto.builder()
                .photoId(photo.getId())
                .eventId(photo.getEvent().getId())
                .uploaderName(photo.getUploader().getName())
                .fileName(photo.getFileName())
                .photoUrl("/api/v1/events/photos/" + photo.getId())
                .uploadedAt(photo.getUploadedAt())
                .build();
    }
}
