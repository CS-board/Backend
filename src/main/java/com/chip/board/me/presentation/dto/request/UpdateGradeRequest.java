package com.chip.board.me.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateGradeRequest(
        @Min(value = 1, message = "학년은 1 이상이어야 합니다.")
        @Max(value = 6, message = "학년은 6 이하여야 합니다.")
        int grade
) {
}
