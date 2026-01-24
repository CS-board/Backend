package com.chip.board.qnaboard.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateQuestionRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank String content
) {}
