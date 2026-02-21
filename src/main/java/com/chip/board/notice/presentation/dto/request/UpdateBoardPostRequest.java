package com.chip.board.notice.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBoardPostRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String content,
        boolean pinned
) {}