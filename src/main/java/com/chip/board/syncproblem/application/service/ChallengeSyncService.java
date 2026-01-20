package com.chip.board.syncproblem.application.service;

import com.chip.board.baselinesync.application.port.api.SolvedAcPort;
import com.chip.board.baselinesync.application.port.solvedproblem.UserSolvedProblemPort;
import com.chip.board.baselinesync.application.port.syncstate.SyncStateCommandPort;
import com.chip.board.baselinesync.application.port.syncstate.SyncStateQueryPort;
import com.chip.board.baselinesync.domain.CreditedAtMode;
import com.chip.board.baselinesync.infrastructure.persistence.dto.DeltaPageTarget;
import com.chip.board.baselinesync.infrastructure.persistence.dto.SolvedProblemItem;
import com.chip.board.baselinesync.infrastructure.persistence.dto.SyncTarget;
import com.chip.board.challenge.application.port.ChallengeSavePort;
import com.chip.board.challenge.domain.Challenge;
import com.chip.board.challenge.domain.ChallengeStatus;
import com.chip.board.score.application.port.ChallengeUserResultPort;
import com.chip.board.syncproblem.application.port.ChallengeDeltaJobQueuePort;
import com.chip.board.syncproblem.application.port.ChallengeObserveJobQueuePort;
import com.chip.board.challenge.application.port.ChallengeSyncIndexPort;
import com.chip.board.score.application.port.ScoreEventPort;
import com.chip.board.syncproblem.application.port.dto.ChallengeSyncSnapshot;
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

    private final SyncStateQueryPort syncStateQueryPort;
    private final SyncStateCommandPort syncStateCommandPort;

    private final UserSolvedProblemPort userSolvedProblemPort;
    private final ScoreEventPort scoreEventPort;
    private final ChallengeUserResultPort challengeUserResultPort;
    private final SolvedAcPort solvedAcPort;

    private final TransactionTemplate tx;

    private final ChallengeObserveJobQueuePort observeQueuePort;
    private final ChallengeDeltaJobQueuePort deltaJobQueuePort;

    private final ChallengeSyncIndexPort syncIndexPort;

    private final ChallengeSavePort challengeSavePort;

    public void tickOnce(LocalDateTime windowStart) {
        boolean cooldownActive = solvedAcPort.isCooldownActive();

        Optional<ChallengeSyncSnapshot> snapOpt = syncIndexPort.load();
        if (snapOpt.isEmpty()) return;

        ChallengeSyncSnapshot snap = snapOpt.get();
        if (snap.status() == ChallengeStatus.CLOSED && snap.closeFinalized()) return;

        CreditedAtMode upsertMode = decideUpsertMode(snap);
        boolean scoringPhase = isScoringPhase(snap);

        long now = System.currentTimeMillis();

        boolean apiQueuesDrained = false;

        if (!cooldownActive) {
            // 1) observe (API 1회)
            Optional<Long> observeUserIdOpt = observeQueuePort.popDueUserId(now);
            if (observeUserIdOpt.isPresent()) {
                long userId = observeUserIdOpt.get();

                Optional<SyncTarget> tOpt =
                        tx.execute(st -> syncStateQueryPort.findObserveTarget(userId, windowStart));
                if (tOpt == null || tOpt.isEmpty()) return;

                SyncTarget t = tOpt.get();

                Integer solvedCount = solvedAcPort.fetchSolvedCountOrNull(t.bojHandle());
                if (solvedCount == null) {
                    observeQueuePort.scheduleAt(userId, solvedAcPort.nextAllowedAtMs());
                    return;
                }

                tx.executeWithoutResult(st -> syncStateCommandPort.updateObserved(userId, solvedCount));

                Optional<DeltaPageTarget> deltaOpt =
                        tx.execute(st -> syncStateQueryPort.findDeltaTarget(userId, windowStart));
                if (deltaOpt != null && deltaOpt.isPresent()) {
                    deltaJobQueuePort.scheduleAt(userId, solvedAcPort.nextAllowedAtMs());
                }
                return;
            }

            // 2) delta (API 1회)
            Optional<Long> deltaUserIdOpt = deltaJobQueuePort.popDueUserId(now);
            if (deltaUserIdOpt.isPresent()) {
                long userId = deltaUserIdOpt.get();

                Optional<DeltaPageTarget> deltaOpt =
                        tx.execute(st -> syncStateQueryPort.findDeltaTarget(userId, windowStart));
                if (deltaOpt == null || deltaOpt.isEmpty()) return;

                DeltaPageTarget delta = deltaOpt.get();

                List<SolvedProblemItem> items =
                        solvedAcPort.fetchSolvedProblemPageOrNull(delta.bojHandle(), delta.nextPage());
                if (items == null) {
                    deltaJobQueuePort.scheduleAt(userId, solvedAcPort.nextAllowedAtMs());
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

                if (needMore) deltaJobQueuePort.scheduleAt(userId, solvedAcPort.nextAllowedAtMs());
                return;
            }

            // observe/delta 모두 못 뽑았으면(=할 API 작업 없음)
            apiQueuesDrained = true;
        }

        // 3) scoring (DB only)
        if (scoringPhase) {
            Boolean didScore = tx.execute(st -> {
                Optional<Long> scoreTargetUserIdOpt = syncStateQueryPort.pickOneForScoring();
                if (scoreTargetUserIdOpt.isEmpty()) return false;

                long userId = scoreTargetUserIdOpt.get();
                scoreEventPort.insertScoreEventsForUncredited(snap.challengeId(), userId);
                scoreEventPort.fillCreditedAtFromScoreEvent(userId);
                return true;
            });
            if (Boolean.TRUE.equals(didScore)) return;
            // 여기까지 왔으면 scoring 대상도 없음
        }

        // ✅ 의도대로: observe/delta가 비었고(=drained), scoring 대상도 없을 때만 종료 처리 + delete
        if (apiQueuesDrained) {
            tx.executeWithoutResult(st -> {
                challengeUserResultPort.refreshStatsForChallenge(snap.challengeId());
                challengeUserResultPort.rolloverAndRecalculateRanks(snap.challengeId());

                Challenge managed = challengeSavePort.getByIdForUpdate(snap.challengeId());
                if (managed.getStatus() == ChallengeStatus.ACTIVE && !managed.isPrepareFinalized()) {
                    managed.finalizePrepare();
                }
                if (managed.getStatus() == ChallengeStatus.CLOSED && !managed.isCloseFinalized()) {
                    managed.finalizeClose();
                }
            });

            syncIndexPort.delete();
        }
    }


    private static CreditedAtMode decideUpsertMode(ChallengeSyncSnapshot s) {
        if (isScoringPhase(s)) return CreditedAtMode.SCOREABLE_NULL;
        return CreditedAtMode.SEAL_NOW;
    }

    private static boolean isScoringPhase(ChallengeSyncSnapshot s) {
        return (s.status() == ChallengeStatus.ACTIVE && s.prepareFinalized())
                || (s.status() == ChallengeStatus.CLOSED && !s.closeFinalized());
    }
}
