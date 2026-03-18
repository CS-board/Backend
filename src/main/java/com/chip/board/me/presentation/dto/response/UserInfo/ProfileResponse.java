package com.chip.board.me.presentation.dto.response.UserInfo;

import com.chip.board.register.domain.Role;

public record ProfileResponse(String name, String department, Role role) {
}
