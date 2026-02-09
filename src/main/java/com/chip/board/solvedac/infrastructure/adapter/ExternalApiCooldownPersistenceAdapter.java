package com.chip.board.solvedac.infrastructure.adapter;

import com.chip.board.solvedac.application.port.cooldown.ExternalApiCooldownPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class ExternalApiCooldownPersistenceAdapter implements ExternalApiCooldownPort {

    private final JdbcTemplate jdbcTemplate;

    private static final int STATUS_429_COOLDOWN = 1;

    @Override
    public void upsert429Cooldown(String apiKey, LocalDateTime cooldownStartedAtUtc) {
        jdbcTemplate.update(
                """
                INSERT INTO external_api_cooldown (api_key, cooldown_started_at, status)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    cooldown_started_at = VALUES(cooldown_started_at),
                    status = VALUES(status)
                """,
                apiKey,
                Timestamp.valueOf(cooldownStartedAtUtc),
                STATUS_429_COOLDOWN
        );
    }
}
