package com.chip.board.oauth.application.component.reader;

import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.base.exception.ServiceException;
import com.chip.board.oauth.application.component.port.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenFinder {

    private final RefreshTokenRepository refreshTokenRepository;

    public Long findUserIdByTokenOrThrow(String rawRefreshToken) {
        return refreshTokenRepository.findUserIdByToken(rawRefreshToken)
                .orElseThrow(() -> new ServiceException(ErrorCode.REFRESH_TOKEN_INVALID));
    }
}
