package com.chip.board.qnaboard.presentation.dto.response;

import java.util.List;

public record QuestionListResponse(
        List<QuestionSummaryResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
