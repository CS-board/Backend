package com.chip.board.challenge.application.service;

import com.chip.board.challenge.application.port.ChallengeLoadPort;
import com.chip.board.challenge.application.port.ChallengeSavePort;
import com.chip.board.challenge.domain.Challenge;
import com.chip.board.challenge.domain.ChallengeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChallengeStatusService {

    private final ChallengeLoadPort challengeLoadPort;
    private final ChallengeSavePort challengeSavePort;
    private final Clock clock;

    @Transactional
    public void updateChallengeStatus() {
        LocalDateTime now = LocalDateTime.now(clock);

        // ACTIVE/SCHEDULED 중 하나를 가져오되, 우선순위/정렬은 Port 구현에서 책임
        challengeLoadPort.findFirstOpen()
                .ifPresent(challenge -> {
                    boolean changed = updateOne(challenge, now);
                    if (changed) {
                        // JPA면 dirty checking으로도 되지만, Port 추상화 관점에선 명시 save가 안전
                        challengeSavePort.save(challenge);
                    }
                });
    }

    private boolean updateOne(Challenge challenge, LocalDateTime now) {
        if (shouldClose(challenge, now)) {
            challenge.close();
            return true;
        }

        if (shouldActivate(challenge, now)) {
            challenge.activate(now);
            return true;
        }

        return false;
    }

    private boolean shouldClose(Challenge challenge, LocalDateTime now) {
        return !now.isBefore(challenge.getEndAt())
                && challenge.getStatus() != ChallengeStatus.CLOSED;
    }

    private boolean shouldActivate(Challenge challenge, LocalDateTime now) {
        return challenge.getStatus() == ChallengeStatus.SCHEDULED
                && !now.isBefore(challenge.getStartAt())
                && now.isBefore(challenge.getEndAt());
    }
}
