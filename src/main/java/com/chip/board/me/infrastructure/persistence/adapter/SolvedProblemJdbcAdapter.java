package com.chip.board.me.infrastructure.persistence.adapter;

import com.chip.board.me.application.port.SolvedProblemQueryPort;
import com.chip.board.me.infrastructure.persistence.repository.SolvedProblemJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SolvedProblemJdbcAdapter implements SolvedProblemQueryPort {

    private final SolvedProblemJdbcRepository SolvedProblemJdbcRepository;

    @Override
    public List<Row> findByUserIdAndChallengeId(long userId, long challengeId) {
        return SolvedProblemJdbcRepository.findByUserIdAndChallengeId(userId, challengeId);
    }
}