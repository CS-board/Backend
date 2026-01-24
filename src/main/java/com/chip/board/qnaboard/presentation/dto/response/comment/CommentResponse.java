package com.chip.board.qnaboard.presentation.dto.response.comment;

public record CommentResponse(
        long id,
        long authorId,
        String authorName,
        String content,
        String timeAgo
) {}