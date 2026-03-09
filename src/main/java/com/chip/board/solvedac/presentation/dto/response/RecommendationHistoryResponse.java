package com.chip.board.solvedac.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationHistoryResponse(
        long count,          // 예: 5
        int maxCount,        // 예: 300
        List<Item> items
) {
    public record Item(
            Long savedId,         // 삭제 버튼에 필요
            Integer problemId,    // #14655
            String titleKo,       // 문제 제목
            Integer level,        // solved.ac level(1~30)
            String tier,          // "s5", "g3" 같은 코드(색/표시용)
            LocalDateTime savedAt // 2026-03-08 19:00:54 표시용
    ) {}
}