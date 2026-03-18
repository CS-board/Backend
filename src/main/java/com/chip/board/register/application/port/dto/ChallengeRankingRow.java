package com.chip.board.register.application.port.dto;

public record ChallengeRankingRow(
        long userId,
        String name,
        int grade,
        String bojId,
        String studentId,
        String department,
        int solvedCount,
        long totalPoints,
        Integer lastRankNo,
        Integer currentRankNo,
        Integer delta
) {
}
