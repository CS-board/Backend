package com.chip.board.me.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {
    private final String name;
    private final String department;
}
