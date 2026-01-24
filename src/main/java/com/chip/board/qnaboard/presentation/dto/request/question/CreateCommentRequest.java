package com.chip.board.qnaboard.presentation.dto.request.question;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
        @NotBlank String content
) {}
