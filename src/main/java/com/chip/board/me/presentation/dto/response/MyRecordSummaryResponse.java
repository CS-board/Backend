package com.chip.board.me.presentation.dto.response;

public record MyRecordSummaryResponse(
        long maxPoints,       // 최고점수
        int totalSolvedCount  // 총 문제 수
) {}