package com.chip.board.baselinesync.application.port.baselineJob;

import java.util.Optional;

public interface BaselineJobQueuePort {
    Optional<Long> popDueUserId(long nowMs);
    void scheduleAt(long userId, long dueAtMs);
}