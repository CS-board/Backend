package com.chip.board.oauth.infrastructure.persistence;

import com.chip.board.global.jwt.properties.RefreshTokenProperties;
import com.chip.board.global.jwt.token.refresh.RefreshTokenData;
import com.chip.board.oauth.application.component.port.RefreshTokenRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private static final String TOKEN_KEY_PREFIX = "refreshToken:";      // refreshToken:{token}
    private static final String USER_INDEX_PREFIX = "refreshTokenUser:"; // refreshTokenUser:{userId}

    private final RedisTemplate<String, String> redisTemplate;
    private final ValueOperations<String, String> valueOps;
    private final SetOperations<String, String> setOps;
    private final RefreshTokenProperties properties;

    public RedisRefreshTokenRepository(
            RedisTemplate<String, String> redisTemplate,
            RefreshTokenProperties properties
    ) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
        this.setOps = redisTemplate.opsForSet();
        this.properties = properties;
    }

    @Override
    public void save(final RefreshTokenData refreshToken) {
        Objects.requireNonNull(refreshToken, "refreshToken must not be null");
        Objects.requireNonNull(refreshToken.token(), "token must not be null");
        Objects.requireNonNull(refreshToken.userId(), "userId must not be null");

        String tokenKey = buildTokenKey(refreshToken.token());
        String userIndexKey = buildUserIndexKey(refreshToken.userId());
        long ttlSeconds = properties.ttlSeconds();

        // 1) 토큰 키 저장: refreshToken:{token} -> userId
        valueOps.set(tokenKey, String.valueOf(refreshToken.userId()), ttlSeconds, TimeUnit.SECONDS);

        // 2) 유저별 인덱스 Set에 토큰 키 등록
        setOps.add(userIndexKey, tokenKey);
        // 인덱스 키도 토큰과 유사한 TTL을 잡아준다.
        redisTemplate.expire(userIndexKey, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Optional<Long> findUserIdByToken(final String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        String tokenKey = buildTokenKey(token);
        String userIdStr = valueOps.get(tokenKey);

        if (userIdStr == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(Long.parseLong(userIdStr));
        } catch (NumberFormatException e) {
            // 잘못된 값이 저장되어 있으면 안전하게 비우고 empty 반환
            redisTemplate.delete(tokenKey);
            return Optional.empty();
        }
    }

    @Override
    public void deleteByToken(final String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        String tokenKey = buildTokenKey(token);

        String userIdStr = valueOps.get(tokenKey);

        redisTemplate.delete(tokenKey);

        if (userIdStr == null) {
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            return;
        }

        String userIndexKey = buildUserIndexKey(userId);
        setOps.remove(userIndexKey, tokenKey);
    }

    @Override
    public void deleteAllByUserId(final Long userId) {
        if (userId == null) {
            return;
        }

        String userIndexKey = buildUserIndexKey(userId);

        Set<String> tokenKeys = setOps.members(userIndexKey);
        if (tokenKeys != null && !tokenKeys.isEmpty()) {
            redisTemplate.delete(tokenKeys);
        }

        redisTemplate.delete(userIndexKey);
    }

    private String buildTokenKey(String token) {
        return TOKEN_KEY_PREFIX + token;
    }

    private String buildUserIndexKey(Long userId) {
        return USER_INDEX_PREFIX + userId;
    }
}