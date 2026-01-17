package com.chip.board.baselinesync.application.component.writer;

import com.chip.board.baselinesync.application.component.reader.BaselineJobReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BaselineJobWriter {

    private final StringRedisTemplate redis;

    public void scheduleNow(long userId) {
        scheduleAt(userId, System.currentTimeMillis());
    }

    public void scheduleAt(long userId, long atMs) {
        redis.opsForZSet().add(BaselineJobReader.KEY, Long.toString(userId), (double) atMs);
    }
}