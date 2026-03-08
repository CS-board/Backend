package com.chip.board.solvedac.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

public record SolvedAcRandomProblemsRequest(
        @Size(max = 20)
        List<
                @Pattern(regexp = "^[a-z0-9_]+$", message = "algorithmTags는 소문자/숫자/_ 만 허용합니다.")
                        String
                > algorithmTags,

        @Min(0) Integer minSolvedUserCount,
        @Min(0) Integer maxSolvedUserCount,

        @NotBlank @Pattern(regexp = "^[a-z0-9]+$", message = "tierFrom은 소문자/숫자 만 허용합니다.") String tierFrom,
        @NotBlank @Pattern(regexp = "^[a-z0-9]+$", message = "tierTo는 소문자/숫자 만 허용합니다.") String tierTo,

        @NotNull SolvedFilter solvedFilter,

        @NotNull
        @Min(1) @Max(50)
        Integer limit
) {
    public enum SolvedFilter {
        ANY, SOLVED, UNSOLVED
    }
}
