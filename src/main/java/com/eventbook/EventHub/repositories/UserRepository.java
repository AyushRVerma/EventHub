package com.eventbook.EventHub.repositories;


import com.eventbook.EventHub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
