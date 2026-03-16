package com.chip.board.me.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateDepartmentRequest(
        @NotBlank(message = "학과는 필수입니다.")
        String department
) {
}
