package com.chip.board.solvedac.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record SaveProblemsRequest(
        @NotNull
        @Size(min = 1, max = 50)
        List<@Valid Item> items
) {
    public record Item(
            @NotNull @Min(1) Integer problemId,
            @NotBlank String titleKo,
            @NotNull @Min(0) Integer level
    ) {}
}