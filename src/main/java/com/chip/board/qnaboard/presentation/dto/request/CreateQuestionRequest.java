package com.chip.board.qnaboard.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateQuestionRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank String content
) {}
