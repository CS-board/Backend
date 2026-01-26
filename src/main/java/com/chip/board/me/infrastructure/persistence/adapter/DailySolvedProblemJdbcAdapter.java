package com.chip.board.me.infrastructure.persistence.adapter;

import com.chip.board.me.application.port.DailySolvedProblemQueryPort;
import com.chip.board.me.infrastructure.persistence.repository.DailySolvedProblemJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DailySolvedProblemJdbcAdapter implements DailySolvedProblemQueryPort {

    private final DailySolvedProblemJdbcRepository dailySolvedProblemJdbcRepository;

    @Override
    public List<Row> findByUserIdAndChallengeIdBetween(
            long userId,
            long challengeId,
            LocalDateTime startInclusive,
            LocalDateTime endExclusive
    ) {
        return dailySolvedProblemJdbcRepository.findByUserIdAndChallengeIdBetween(userId, challengeId, startInclusive, endExclusive);
    }
}