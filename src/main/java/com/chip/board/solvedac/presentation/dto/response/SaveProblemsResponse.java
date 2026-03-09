package com.chip.board.solvedac.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record SaveProblemsResponse(
        int savedCount,
        int skippedCount,
        int evictedCount,
        List<Item> items
) {
    public record Item(
            Long savedId,
            Integer problemId,
            String titleKo,
            Integer level,
            String tier,
            LocalDateTime savedAt
    ) {}
}