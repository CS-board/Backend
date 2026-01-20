package com.chip.board.register.application.component.reader;

import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.base.exception.ServiceException;
import com.chip.board.register.application.port.UserRepositoryPort;
import com.chip.board.register.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFinder {
    private final UserRepositoryPort userRepositoryPort;

    public User findById(Long userId) {
        return userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
    }
}
