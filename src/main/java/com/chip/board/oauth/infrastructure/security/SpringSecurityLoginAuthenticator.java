package com.chip.board.oauth.infrastructure.security;

import com.chip.board.oauth.application.component.port.LoginAuthenticator;
import com.chip.board.oauth.presentation.dto.request.LoginRequest;
import com.chip.board.register.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringSecurityLoginAuthenticator implements LoginAuthenticator {

    private final AuthenticationManager authenticationManager;

    @Override
    public CustomUserDetails authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return (CustomUserDetails) authentication.getPrincipal();
    }
}