package com.chip.board.me.application.service;

import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.base.exception.ServiceException;
import com.chip.board.oauth.application.component.writer.RefreshTokenWriter;
import com.chip.board.register.application.port.UserRepositoryPort;
import com.chip.board.register.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserWithdrawalService {

    private final UserRepositoryPort userRepositoryPort;
    private final RefreshTokenWriter refreshTokenWriter;

    @Transactional
    public void withdraw(Long userId, String rawRefreshToken) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        if (rawRefreshToken != null) {
            refreshTokenWriter.deleteByToken(rawRefreshToken);
        }

        user.withdraw();
    }
}