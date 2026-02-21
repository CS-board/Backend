package com.chip.board.notice.presentation.dto.response;

import com.chip.board.notice.domain.BoardPost;

import java.time.LocalDateTime;

public record BoardPostDetailResponse(
        Long id,
        String title,
        String content,
        boolean pinned,
        LocalDateTime createdAt
) {
    public static BoardPostDetailResponse from(BoardPost boardPost) {
        return new BoardPostDetailResponse(
                boardPost.getId(), boardPost.getTitle(), boardPost.getContent(), boardPost.isPinned(), boardPost.getCreatedAt()
        );
    }
}
