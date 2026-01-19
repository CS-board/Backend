package com.chip.board.syncproblem.application.service;

import com.chip.board.baselinesync.application.port.syncstate.SyncStateQueryPort;
import com.chip.board.syncproblem.application.port.ChallengeObserveJobQueuePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeObserveEnqueueService {

    private final ChallengeObserveJobQueuePort observeQueuePort;
    private final SyncStateQueryPort syncStateQueryPort;

    public void enqueueObserveWindow(LocalDateTime windowStart) {
        List<Long> userIds = syncStateQueryPort.findObserveUserIds(windowStart);
        long now = System.currentTimeMillis();
        for (Long userId : userIds) {
            observeQueuePort.scheduleAt(userId, now);
        }
    }
}