package com.chip.board.qnaboard.infrastructure.persistence.dto;

import java.time.LocalDateTime;

public interface QuestionSummaryRow {
    Long getId();
    String getTitle();
    String getAuthorName();
    LocalDateTime getCreatedAt();
    boolean isSolved();
    long getCommentCount();
    long getLikeCount();
}
