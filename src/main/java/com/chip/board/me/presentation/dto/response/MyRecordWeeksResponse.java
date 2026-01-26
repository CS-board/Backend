package com.chip.board.me.presentation.dto.response;

import java.util.List;

public record MyRecordWeeksResponse(
        List<MyRecordWeekItem> items,
        int page,
        int size,
        boolean hasNext
) {
    public record MyRecordWeekItem(
            String week,       // 예: 2024-W03
            int solvedCount,   // 주간 해결
            long score,        // 점수
            Integer rank       // 주간 순위 (없으면 null)
    ) {}
}
