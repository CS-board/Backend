package com.chip.board.solvedac.application.port.cooldown;

import java.time.LocalDateTime;

public interface ExternalApiCooldownPort {
    void upsert429Cooldown(String apiKey, LocalDateTime cooldownStartedAt);
}
