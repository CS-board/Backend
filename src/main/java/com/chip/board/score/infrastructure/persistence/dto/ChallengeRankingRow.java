package com.chip.board.score.infrastructure.persistence.dto;

public record ChallengeRankingRow(
        long userId,
        String name,
        String bojId,
        String department,
        int solvedCount,
        long totalPoints,
        Integer lastRankNo,
        Integer currentRankNo,
        Integer delta
) {
}
