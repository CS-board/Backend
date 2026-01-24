package com.chip.board.qnaboard.presentation.dto.response;

public record QuestionSummaryResponse(
        long id,
        String title,
        String authorName,
        String timeAgo,
        boolean solved,
        long commentCount,
        long likeCount
) {}

