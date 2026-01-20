package com.chip.board.oauth.infrastructure.redis;

import com.chip.board.oauth.application.port.PasswordResetCodeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class RedisPasswordResetCodeStore implements PasswordResetCodeStore {

    private final StringRedisTemplate redis;
    private static final String KEY_PREFIX = "auth:password-reset:";

    private String key(String email) {
        if (email == null) {
            throw new IllegalArgumentException("email must not be null");
        }
        return KEY_PREFIX + email.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public void saveAuthCode(String email, int code, Duration ttl) {
        redis.opsForValue().set(key(email), String.valueOf(code), ttl);
    }

    @Override
    public String get(String email) {
        return redis.opsForValue().get(key(email));
    }

    @Override
    public void markVerified(String email, Duration ttl) {
        redis.opsForValue().set(key(email), "VERIFIED", ttl);
    }

    @Override
    public void delete(String email) {
        redis.delete(key(email));
    }
}
