package com.chip.board.me.presentation.dto.response.UserRecord;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record SolvedProblemsResponse(
        LocalDate solvedDate,
        int dailySolvedCount,
        List<Item> items
) {
    public record Item(
            int problemId,
            String titleKo,
            int level,
            String tierName,
            int points,
            Instant solvedAt
    ) {}
}