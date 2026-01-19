package com.chip.board.syncproblem.infrastructure.persistence.adapter;

import com.chip.board.global.infrastructure.RedisZSetJobQueueRepository;
import com.chip.board.syncproblem.application.port.ChallengeDeltaJobQueuePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChallengeDeltaJobQueueAdapter implements ChallengeDeltaJobQueuePort {

    private static final String KEY = "solvedac:challenge:delta:jobs";

    private final RedisZSetJobQueueRepository repo;

    @Override
    public Optional<Long> popDueUserId(long nowMs) {
        return repo.popDueUserId(KEY, nowMs);
    }

    @Override
    public void scheduleAt(long userId, long dueAtMs) {
        repo.scheduleAt(KEY, userId, dueAtMs);
    }
}
