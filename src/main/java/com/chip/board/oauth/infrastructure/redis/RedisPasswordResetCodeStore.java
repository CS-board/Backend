package com.chip.board.oauth.infrastructure.redis;

import com.chip.board.oauth.application.port.PasswordResetCodeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisPasswordResetCodeStore implements PasswordResetCodeStore {

    private final StringRedisTemplate redis;
    private static final String KEY_PREFIX = "auth:password-reset:";

    @Override
    public void saveAuthCode(String email, int code, Duration ttl) {
        redis.opsForValue().set(KEY_PREFIX + email, String.valueOf(code), ttl);
    }

    @Override
    public String get(String email) {
        return redis.opsForValue().get(KEY_PREFIX + email);
    }

    @Override
    public void markVerified(String email, Duration ttl) {
        redis.opsForValue().set(KEY_PREFIX + email, "VERIFIED", ttl);
    }

    @Override
    public void delete(String email) {
        redis.delete(KEY_PREFIX + email);
    }
}