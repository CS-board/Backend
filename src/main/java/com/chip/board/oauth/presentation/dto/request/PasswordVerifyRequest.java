package com.chip.board.oauth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PasswordVerifyRequest(
        @NotBlank
        @Pattern(regexp = ".+@kumoh.ac.kr$", message = "이메일 형식이 올바르지 않습니다.")
        String username,

        @NotNull
        String mailCode
) {}
