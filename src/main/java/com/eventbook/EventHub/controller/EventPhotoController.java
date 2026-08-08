package com.eventbook.EventHub.controller;

import com.eventbook.EventHub.domain.DTOs.EventPhotoResponseDto;
import com.eventbook.EventHub.services.EventPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static com.eventbook.EventHub.util.JwtUtil.parseUserId;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventPhotoController {

    private final EventPhotoService eventPhotoService;

    @PostMapping("/{eventId}/photos")
    public ResponseEntity<EventPhotoResponseDto> uploadPhoto(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID eventId,
            @RequestParam("file") MultipartFile file) {

        UUID userId = parseUserId(jwt);
        EventPhotoResponseDto response = eventPhotoService.uploadPhoto(eventId, userId, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}/photos")
    public ResponseEntity<List<EventPhotoResponseDto>> getPhotosForEvent(@PathVariable UUID eventId) {
        List<EventPhotoResponseDto> photos = eventPhotoService.getPhotosForEvent(eventId);
        return ResponseEntity.ok(photos);
    }

    @GetMapping("/photos/{photoId}")
    public ResponseEntity<byte[]> viewPhoto(@PathVariable UUID photoId) {
        byte[] imageBytes = eventPhotoService.getPhotoBytes(photoId);
        String contentType = eventPhotoService.getPhotoContentType(photoId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        return ResponseEntity.ok().headers(headers).body(imageBytes);
    }
}
