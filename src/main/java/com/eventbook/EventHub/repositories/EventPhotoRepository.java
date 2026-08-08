package com.eventbook.EventHub.repositories;

import com.eventbook.EventHub.domain.entity.EventPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventPhotoRepository extends JpaRepository<EventPhoto, UUID> {

    int countByEventIdAndUploaderId(UUID eventId, UUID uploaderId);

    List<EventPhoto> findByEventIdOrderByUploadedAtDesc(UUID eventId);
}
