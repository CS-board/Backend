package com.chip.board.syncproblem.application.scheduler;

import com.chip.board.challenge.application.port.ChallengeLoadPort;
import com.chip.board.syncproblem.application.service.ChallengeObserveEnqueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class ChallengeObserveEnqueueScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final ChallengeLoadPort challengeLoadPort;
    private final ChallengeObserveEnqueueService challengeObserveEnqueueService;
    private final Clock clock;

    @Scheduled(cron = "0 42 13 * * *", zone = "Asia/Seoul")
    public void enqueue() {
        boolean shouldRun = shouldRun();
        if (!shouldRun) return;

        LocalDateTime windowStart = LocalDate.now(clock).atTime(13, 42, 0);
        challengeObserveEnqueueService.enqueueObserveWindow(windowStart);
    }

    private boolean shouldRun() {
        return challengeLoadPort.existsActive()
                || challengeLoadPort.existsClosedUnfinalized();
    }
}