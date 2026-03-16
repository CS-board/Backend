package com.chip.board.baselinesync.application.service;

import com.chip.board.baselinesync.application.port.baselineJob.BaselineEnqueuePort;
import com.chip.board.baselinesync.application.port.baselineJob.BaselineJobQueuePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaselineEnqueueService implements BaselineEnqueuePort {

    private final BaselineJobQueuePort jobQueuePort;

    @Override
    public void enqueueBaseline(long userId) {
        long now = System.currentTimeMillis();
        log.info("enqueueBaseline userId={}, now={}", userId, now);
        jobQueuePort.scheduleAt(userId, now);
    }
}