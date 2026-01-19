package com.chip.board.global.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisZSetJobQueueRepository {

    private final StringRedisTemplate redis;

    public Optional<Long> popDueUserId(String key, long nowMs) {
        ZSetOperations<String, String> zset = redis.opsForZSet();

        Set<ZSetOperations.TypedTuple<String>> first = zset.rangeWithScores(key, 0, 0);
        if (first == null || first.isEmpty()) return Optional.empty();

        ZSetOperations.TypedTuple<String> tuple = first.iterator().next();
        if (tuple == null || tuple.getValue() == null || tuple.getScore() == null) return Optional.empty();

        if (nowMs < tuple.getScore().longValue()) return Optional.empty();

        Set<ZSetOperations.TypedTuple<String>> popped = zset.popMin(key, 1);
        if (popped == null || popped.isEmpty()) return Optional.empty();

        String userIdStr = popped.iterator().next().getValue();
        if (userIdStr == null) return Optional.empty();

        try { return Optional.of(Long.parseLong(userIdStr)); }
        catch (NumberFormatException e) { return Optional.empty(); }
    }

    public void scheduleAt(String key, long userId, long dueAtMs) {
        redis.opsForZSet().add(key, Long.toString(userId), (double) dueAtMs);
    }
}