package com.eventbook.EventHub.Config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseMigrationRunner {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void dropOldCheckConstraints() {
        try {
            // Drop old PostgreSQL enum check constraint so new statuses (PENDING_PAYMENT, EXPIRED) are accepted
            jdbcTemplate.execute("ALTER TABLE tickets DROP CONSTRAINT IF EXISTS tickets_status_check");
            log.info("Successfully dropped old tickets_status_check constraint from PostgreSQL!");
        } catch (Exception e) {
            log.warn("Could not drop tickets_status_check constraint: {}", e.getMessage());
        }
    }
}
