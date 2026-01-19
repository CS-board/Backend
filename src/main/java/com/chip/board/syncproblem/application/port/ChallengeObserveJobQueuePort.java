package com.chip.board.syncproblem.application.port;

import java.util.Optional;

public interface ChallengeObserveJobQueuePort {
    Optional<Long> popDueUserId(long nowMs);
    void scheduleAt(long userId, long dueAtMs);
}
