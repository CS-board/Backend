package com.chip.board.oauth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PasswordResetRequest(
        @NotBlank
        @Pattern(regexp = ".+@kumoh.ac.kr$", message = "이메일 형식이 올바르지 않습니다.")
        String username,

        @NotNull
        @Pattern(
                message = "비밀번호는 최소 10자 이상~20자 이하, 영문 대문자, 소문자, 특수문자를 포함해야 합니다.",
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@!#$%^&*()_+?])[A-Za-z\\d@!#$%^&*()_+?]{10,20}$"
        )
        String newPassword
) {}