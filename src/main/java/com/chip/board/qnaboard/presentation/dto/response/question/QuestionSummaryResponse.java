package com.chip.board.qnaboard.presentation.dto.response.question;

public record QuestionSummaryResponse(
        Long  id,
        String title,
        String authorStudentId,
        String authorName,
        String timeAgo,
        boolean solved,
        long commentCount,
        long likeCount
) {}

