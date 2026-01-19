package com.chip.board.challenge.infrastructure.persistence.adapter;

import com.chip.board.challenge.domain.ChallengeStatus;
import com.chip.board.challenge.application.port.ChallengeSyncIndexPort;
import com.chip.board.syncproblem.application.port.dto.ChallengeSyncSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChallengeSyncIndexRedisAdapter implements ChallengeSyncIndexPort {

    private static final String KEY = "solvedac:challenge:sync:index";

    private static final String F_CHALLENGE_ID = "challengeId";
    private static final String F_STATUS = "status";
    private static final String F_PREPARE_FINALIZED = "prepareFinalized";
    private static final String F_CLOSE_FINALIZED = "closeFinalized";

    private final StringRedisTemplate redis;

    @Override
    public Optional<ChallengeSyncSnapshot> load() {
        try {
            Map<Object, Object> entries = redis.opsForHash().entries(KEY);
            if (entries.isEmpty()) {
                return Optional.empty();
            }

            String cidStr = (String) entries.get(F_CHALLENGE_ID);
            String statusStr = (String) entries.get(F_STATUS);
            String pfStr = (String) entries.get(F_PREPARE_FINALIZED);
            String cfStr = (String) entries.get(F_CLOSE_FINALIZED);

            if (cidStr == null || statusStr == null || pfStr == null || cfStr == null) {
                return Optional.empty();
            }

            long challengeId = Long.parseLong(cidStr);
            ChallengeStatus status = ChallengeStatus.valueOf(statusStr);
            boolean prepareFinalized = "1".equals(pfStr);
            boolean closeFinalized = "1".equals(cfStr);

            return Optional.of(new ChallengeSyncSnapshot(challengeId, status, prepareFinalized, closeFinalized));
        } catch (Exception e) {
            // 파싱 실패/오염 시 self-heal
            redis.delete(KEY);
            return Optional.empty();
        }
    }

    @Override
    public void save(ChallengeSyncSnapshot snapshot) {
        redis.opsForHash().putAll(KEY, Map.of(
                F_CHALLENGE_ID, Long.toString(snapshot.challengeId()),
                F_STATUS, snapshot.status().name(),
                F_PREPARE_FINALIZED, snapshot.prepareFinalized() ? "1" : "0",
                F_CLOSE_FINALIZED, snapshot.closeFinalized() ? "1" : "0"
        ));
    }

    @Override
    public void delete() {
        redis.delete(KEY);
    }
}