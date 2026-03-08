package com.chip.board.solvedac.presentation.dto.response;

import java.util.List;

public record SolvedAcRandomProblemsResponse(
        String query,
        List<Item> items
) {
    public record Item(
            long problemId,
            int level,
            String tier,
            String titleKo
    ) {}
}