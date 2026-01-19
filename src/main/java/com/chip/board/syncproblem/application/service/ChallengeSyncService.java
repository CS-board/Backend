package com.chip.board.syncproblem.application.service;

import com.chip.board.baselinesync.application.port.api.SolvedAcPort;
import com.chip.board.baselinesync.application.port.solvedproblem.UserSolvedProblemPort;
import com.chip.board.baselinesync.application.port.syncstate.SyncStateCommandPort;
import com.chip.board.baselinesync.application.port.syncstate.SyncStateQueryPort;
import com.chip.board.baselinesync.domain.CreditedAtMode;
import com.chip.board.baselinesync.infrastructure.persistence.dto.DeltaPageTarget;
import com.chip.board.baselinesync.infrastructure.persistence.dto.SolvedProblemItem;
import com.chip.board.baselinesync.infrastructure.persistence.dto.SyncTarget;
import com.chip.board.challenge.application.port.ChallengeLoadPort;
import com.chip.board.challenge.application.port.ChallengeSavePort;
import com.chip.board.challenge.domain.Challenge;
import com.chip.board.challenge.domain.ChallengeStatus;
import com.chip.board.syncproblem.application.port.ChallengeDeltaJobQueuePort;
import com.chip.board.syncproblem.application.port.ChallengeObserveJobQueuePort;
import com.chip.board.syncproblem.application.port.ScoreEventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeSyncService {

    private final ChallengeLoadPort challengeLoadPort;
    private final ChallengeSavePort challengeSavePort;

    private final SyncStateQueryPort syncStateQueryPort;
    private final SyncStateCommandPort syncStateCommandPort;

    private final UserSolvedProblemPort userSolvedProblemPort;
    private final ScoreEventPort scoreEventPort;
    private final SolvedAcPort solvedAcPort;

    private final TransactionTemplate tx;

    private final ChallengeObserveJobQueuePort observeQueue;
    private final ChallengeDeltaJobQueuePort deltaQueue;

    public void tickOnce(LocalDateTime windowStart) {
        boolean cooldownActive = solvedAcPort.isCooldownActive();

        Optional<Challenge> challengeOpt = pickChallenge();
        if (challengeOpt.isEmpty()) return;

        Challenge challenge = challengeOpt.get();
        if (challenge.getStatus() == ChallengeStatus.CLOSED && challenge.isCloseFinalized()) return;

        CreditedAtMode upsertMode = decideUpsertMode(challenge);
        boolean scoringPhase = isScoringPhase(challenge);

        long now = System.currentTimeMillis();

        if (!cooldownActive) {
            // 1) observe (API 1회)
            Optional<Long> observeUserIdOpt = observeQueue.popDueUserId(now);
            if (observeUserIdOpt.isPresent()) {
                long userId = observeUserIdOpt.get();

                Optional<SyncTarget> tOpt =
                        tx.execute(st -> syncStateQueryPort.findObserveTarget(userId, windowStart));
                if (tOpt == null || tOpt.isEmpty()) return;

                SyncTarget t = tOpt.get();

                Integer solvedCount = solvedAcPort.fetchSolvedCountOrNull(t.bojHandle());
                if (solvedCount == null) {
                    observeQueue.scheduleAt(userId, solvedAcPort.nextAllowedAtMs());
                    return;
                }

                tx.executeWithoutResult(st -> syncStateCommandPort.updateObserved(userId, solvedCount));

                Optional<DeltaPageTarget> deltaOpt =
                        tx.execute(st -> syncStateQueryPort.findDeltaTarget(userId, windowStart));
                if (deltaOpt != null && deltaOpt.isPresent()) {
                    deltaQueue.scheduleAt(userId, solvedAcPort.nextAllowedAtMs());
                }
                return; // tick당 API 1회
            }

            // 2) delta (API 1회)
            Optional<Long> deltaUserIdOpt = deltaQueue.popDueUserId(now);
            if (deltaUserIdOpt.isPresent()) {
                long userId = deltaUserIdOpt.get();

                Optional<DeltaPageTarget> deltaOpt =
                        tx.execute(st -> syncStateQueryPort.findDeltaTarget(userId, windowStart));
                if (deltaOpt == null || deltaOpt.isEmpty()) return;

                DeltaPageTarget delta = deltaOpt.get();

                List<SolvedProblemItem> items =
                        solvedAcPort.fetchSolvedProblemPageOrNull(delta.bojHandle(), delta.nextPage());
                if (items == null) {
                    deltaQueue.scheduleAt(userId, solvedAcPort.nextAllowedAtMs());
                    return;
                }

                boolean needMore = Boolean.TRUE.equals(
                        tx.execute(st -> {
                            if (items.isEmpty()) {
                                syncStateCommandPort.finishDelta(delta.userId(), delta.observedSolvedCount());
                                return false;
                            }

                            userSolvedProblemPort.upsertBatch(delta.userId(), items, upsertMode);
                            syncStateCommandPort.advancePage(delta.userId());
                            return true;
                        })
                );

                if (needMore) deltaQueue.scheduleAt(userId, solvedAcPort.nextAllowedAtMs());
                return; // tick당 API 1회
            }
        }

        // 3) scoring (DB only)
        if (scoringPhase) {
            Boolean didScore = tx.execute(st -> {
                Optional<Long> scoreTargetUserIdOpt = syncStateQueryPort.pickOneForScoring();
                if (scoreTargetUserIdOpt.isEmpty()) return false;

                long userId = scoreTargetUserIdOpt.get();
                scoreEventPort.insertScoreEventsForUncredited(challenge.getChallengeId(), userId);
                scoreEventPort.fillCreditedAtFromScoreEvent(userId);
                return true;
            });

            if (Boolean.TRUE.equals(didScore)) return;
        }

        // 4) finalize (DB only)
        tx.executeWithoutResult(st -> {
            Challenge managed = challengeSavePort.getByIdForUpdate(challenge.getChallengeId());

            if (managed.getStatus() == ChallengeStatus.ACTIVE && !managed.isPrepareFinalized()) {
                boolean observePendingExists = syncStateQueryPort.existsObservePending(windowStart);
                boolean deltaPendingExists = syncStateQueryPort.existsDeltaPending(windowStart);
                if (!observePendingExists && !deltaPendingExists) managed.finalizePrepare();
                return;
            }

            if (managed.getStatus() == ChallengeStatus.CLOSED && !managed.isCloseFinalized()) {
                boolean observePendingExists = syncStateQueryPort.existsObservePending(windowStart);
                boolean deltaPendingExists = syncStateQueryPort.existsDeltaPending(windowStart);
                boolean scoreableExists = syncStateQueryPort.existsAnyScoreable();
                if (!observePendingExists && !deltaPendingExists && !scoreableExists) managed.finalizeClose();
            }
        });
    }

    private Optional<Challenge> pickChallenge() {
        Optional<Challenge> active = challengeLoadPort.findActive();
        if (active.isPresent()) return active;

        return challengeLoadPort.findLatestUnfinalizedClosed();
    }

    private static CreditedAtMode decideUpsertMode(Challenge c) {
        if (isScoringPhase(c)) return CreditedAtMode.SCOREABLE_NULL;
        return CreditedAtMode.SEAL_NOW;
    }

    private static boolean isScoringPhase(Challenge c) {
        return (c.getStatus() == ChallengeStatus.ACTIVE && c.isPrepareFinalized())
                || (c.getStatus() == ChallengeStatus.CLOSED && !c.isCloseFinalized());
    }
}
