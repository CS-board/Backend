package com.chip.board.oauth.application.component.writer;

import com.chip.board.global.jwt.token.refresh.RefreshTokenData;
import com.chip.board.oauth.application.port.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenWriter {
    private final RefreshTokenRepository refreshTokenRepository;

    public void replaceUserToken(Long userId, RefreshTokenData refreshToken) {
        refreshTokenRepository.deleteAllByUserId(userId);
        refreshTokenRepository.save(refreshToken);
    }

    public void deleteByToken(String rawRefreshToken) {
        refreshTokenRepository.deleteByToken(rawRefreshToken);
    }

    public void save(RefreshTokenData refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }
}