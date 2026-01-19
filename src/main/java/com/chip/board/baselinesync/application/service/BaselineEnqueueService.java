package com.chip.board.baselinesync.application.service;

import com.chip.board.baselinesync.application.port.baselineJob.BaselineEnqueuePort;
import com.chip.board.baselinesync.application.port.baselineJob.BaselineJobQueuePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaselineEnqueueService implements BaselineEnqueuePort {

    private final BaselineJobQueuePort jobQueuePort;

    @Override
    public void enqueueBaseline(long userId) {
        long now = System.currentTimeMillis();
        jobQueuePort.scheduleAt(userId, now);
    }
}