package com.chip.board.qnaboard.presentation.dto.request.comment;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentRequest(
        @NotBlank String content
) {}
