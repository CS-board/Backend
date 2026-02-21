package com.chip.board.notice.presentation.dto.response;

import java.time.LocalDateTime;

public record BoardPostListItemResponse(
        Long id,
        String title,
        boolean pinned,
        boolean isNew,
        LocalDateTime createdAt
) {}
