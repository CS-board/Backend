package com.chip.board.notice.presentation.dto.response;

import com.chip.board.notice.domain.BoardPost;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public record BoardPostListResponse(
        List<BoardPostListItemResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static BoardPostListResponse from(Page<BoardPost> pageData, LocalDateTime now, int newDays) {
        List<BoardPostListItemResponse> items = pageData.getContent().stream()
                .map(p -> new BoardPostListItemResponse(
                        p.getId(),
                        p.getTitle(),
                        p.isPinned(),
                        p.getCreatedAt() != null && p.getCreatedAt().isAfter(now.minusDays(newDays)),
                        p.getCreatedAt()
                ))
                .toList();

        return new BoardPostListResponse(
                items,
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages()
        );
    }
}