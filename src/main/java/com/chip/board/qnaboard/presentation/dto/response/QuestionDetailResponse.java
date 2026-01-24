package com.chip.board.qnaboard.presentation.dto.response;

import java.util.List;

public record QuestionDetailResponse(
        long id,
        String title,
        String content,
        long authorId,
        String authorName,
        String timeAgo,
        boolean solved,
        long commentCount,
        long likeCount,
        List<CommentResponse> comments
) {}
