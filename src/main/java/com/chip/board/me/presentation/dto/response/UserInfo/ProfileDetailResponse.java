package com.chip.board.me.presentation.dto.response.UserInfo;

import com.chip.board.register.domain.Role;

public record ProfileDetailResponse(
        String name,
        String studentId,
        String department,
        int grade,
        String email,
        String bojId,
        long goalPoints,
        Role role
) {
}
