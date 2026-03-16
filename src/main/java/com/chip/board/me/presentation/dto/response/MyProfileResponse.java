package com.chip.board.me.presentation.dto.response;

public record MyProfileResponse(
        String name,
        String studentId,
        String department,
        int grade,
        String email,
        String bojId,
        long goalPoints
) {
}
