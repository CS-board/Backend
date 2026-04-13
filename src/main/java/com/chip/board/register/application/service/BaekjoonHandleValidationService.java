package com.chip.board.register.application.service;

import com.chip.board.solvedac.application.port.api.SolvedAcPort;
import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.base.exception.ServiceException;
import com.chip.board.register.application.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaekjoonHandleValidationService {

    private final SolvedAcPort solvedAcPort;
    private final UserRepositoryPort userRepositoryPort;

    @Transactional(readOnly = true)
    public boolean validate(String handle) {
        boolean alreadyUsed = userRepositoryPort.existsActiveByBojId(handle);
        if (alreadyUsed) {
            log.debug("Baekjoon handle validation failed by duplicate handle. handle={}", handle);
            throw new ServiceException(ErrorCode.DUPLICATE_BOJ_ID);
        }

        boolean cooldownActive = solvedAcPort.isCooldownActive();
        if (cooldownActive) {
            log.debug("Baekjoon handle validation blocked by solved.ac cooldown. handle={}", handle);
            throw new ServiceException(ErrorCode.SOLVEDAC_COOLDOWN_ACTIVE);
        }

        Integer solvedCount = solvedAcPort.fetchSolvedCountOrNull(handle);
        if (solvedCount == null) {
            log.debug("Baekjoon handle validation failed by solved.ac response. handle={}", handle);
            throw new ServiceException(ErrorCode.BAEKJOON_HANDLE_INVALID);
        }
        log.debug("Baekjoon handle validated. handle={}", handle);
        return true;
    }
}
