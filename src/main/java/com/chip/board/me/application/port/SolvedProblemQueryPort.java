package com.chip.board.me.application.port;

import java.time.Instant;
import java.util.List;

public interface SolvedProblemQueryPort {

    List<Row> findByUserIdAndChallengeId(long userId, long challengeId);

    record Row(
            int problemId,
            String titleKo,
            int level,
            String tierName,
            int points,
            Instant solvedAt
    ) {}
}