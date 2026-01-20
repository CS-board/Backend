package com.chip.board.oauth.application.port;

import com.chip.board.global.jwt.token.refresh.RefreshTokenData;

import java.util.Optional;

public interface RefreshTokenRepository {
    void save(RefreshTokenData refreshToken);
    Optional<Long> findUserIdByToken(String token);
    void deleteByToken(String token);
    void deleteAllByUserId(Long userId);
}