package com.chip.board.baselinesync.application.service;

import com.chip.board.solvedac.application.port.api.SolvedAcPort;
import com.chip.board.baselinesync.application.port.baselineJob.BaselineJobQueuePort;
import com.chip.board.baselinesync.application.port.solvedproblem.UserSolvedProblemPort;
import com.chip.board.baselinesync.application.port.syncstate.SyncStateCommandPort;
import com.chip.board.baselinesync.application.port.syncstate.SyncStateQueryPort;
import com.chip.board.baselinesync.domain.CreditedAtMode;
import com.chip.board.solvedac.infrastructure.api.dto.response.SolvedProblemPage;
import com.chip.board.baselinesync.infrastructure.persistence.dto.BaselineTarget;
import com.chip.board.baselinesync.infrastructure.persistence.dto.SolvedProblemItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaselineFillService {

    private final SyncStateQueryPort syncStateQueryPort;
    private final SyncStateCommandPort syncStateCommandPort;
    private final UserSolvedProblemPort solvedProblemPort;
    private final SolvedAcPort solvedAcPort;
    private final TransactionTemplate tx;

    private final BaselineJobQueuePort jobQueuePort;

    /** 스케줄러에서 자주 호출해도 됨(전역 gate에서 대부분 return) */
    public void tickOnce() {
        if (solvedAcPort.isCooldownActive()) return;

        long now = System.currentTimeMillis();

        Optional<Long> userIdOpt = jobQueuePort.popDueUserId(now);
        if (userIdOpt.isEmpty()) return;

        long userId = userIdOpt.get();

        Optional<BaselineTarget> targetOpt =
                tx.execute(st -> syncStateQueryPort.findBaselineTarget(userId));
        if (targetOpt == null || targetOpt.isEmpty()) return;

        BaselineTarget target = targetOpt.get();
        String handle = target.bojHandle();
        int nextPage = target.nextPage();

        if (handle == null || handle.isBlank()) {
            tx.executeWithoutResult(st -> syncStateCommandPort.markBaselineReady(userId));
            return;
        }

        // ✅ tick당 API 호출 1회 (count + items)
        SolvedProblemPage page = solvedAcPort.fetchSolvedProblemPageWithCountOrNull(handle, nextPage);
        if (page == null) {
            jobQueuePort.scheduleAt(userId, solvedAcPort.nextAllowedAtMs());
            return;
        }

        int totalSolvedCount = page.totalSolvedCount();
        List<SolvedProblemItem> items = page.items();

        boolean needMore = Boolean.TRUE.equals(
                tx.execute(st -> {
                    syncStateCommandPort.initLastSolvedCountOnce(userId, totalSolvedCount);

                    if (items.isEmpty()) {
                        syncStateCommandPort.markBaselineReady(userId);
                        return false;
                    }

                    solvedProblemPort.upsertBatch(userId, items, CreditedAtMode.SEAL_NOW);
                    syncStateCommandPort.advancePage(userId);
                    return true;
                })
        );

        if (needMore) {
            jobQueuePort.scheduleAt(userId, solvedAcPort.nextAllowedAtMs());
        }
    }
}
