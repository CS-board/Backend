package com.chip.board.baselinesync.infrastructure.adapter;

import com.chip.board.baselinesync.application.port.baselineJob.BaselineJobQueuePort;
import com.chip.board.global.infrastructure.RedisZSetJobQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BaselineJobQueueAdapter implements BaselineJobQueuePort {

    // 기존 BaselineJobReader/Writer가 쓰던 KEY로 맞추세요(운영 호환)
    private static final String KEY = "solvedac:baseline:jobs";

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
