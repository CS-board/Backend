package com.chip.board.notice.presentation.dto.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record CreateBoardPostRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String content,
        boolean pinned
) {}