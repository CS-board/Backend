package com.chip.board.oauth.application.port;

import com.chip.board.oauth.presentation.dto.request.LoginRequest;
import com.chip.board.register.domain.CustomUserDetails;

public interface LoginAuthenticator {
    CustomUserDetails authenticate(LoginRequest request);
}