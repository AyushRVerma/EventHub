package com.eventbook.EventHub.controller;

import com.eventbook.EventHub.domain.entity.Role;
import com.eventbook.EventHub.domain.entity.User;
import com.eventbook.EventHub.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    /**
     * Endpoint to allow an Admin to update a User's role (e.g. promote ATTENDEE to ORGANIZER).
     */
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserRole(
            @PathVariable UUID userId,
            @RequestParam Role role) {

        return userRepository.findById(userId)
                .map(user -> {
                    user.setRole(role);
                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
