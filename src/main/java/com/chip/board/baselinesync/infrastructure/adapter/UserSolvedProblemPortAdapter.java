package com.chip.board.baselinesync.infrastructure.adapter;

import com.chip.board.baselinesync.application.port.solvedproblem.UserSolvedProblemPort;
import com.chip.board.baselinesync.domain.CreditedAtMode;
import com.chip.board.baselinesync.infrastructure.persistence.dto.SolvedProblemItem;
import com.chip.board.baselinesync.infrastructure.persistence.repository.UserSolvedProblemJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserSolvedProblemPortAdapter implements UserSolvedProblemPort {

    private final UserSolvedProblemJdbcRepository repo;

    @Override
    public void upsertBatch(long userId, List<SolvedProblemItem> items, CreditedAtMode mode) {
        if (items == null || items.isEmpty()) return;

        // infra dto로 변환
        List<com.chip.board.baselinesync.infrastructure.persistence.dto.SolvedProblemItem> mapped =
                items.stream()
                        .map(i -> new com.chip.board.baselinesync.infrastructure.persistence.dto.SolvedProblemItem(i.problemId(), i.level()))
                        .toList();

        repo.upsertBatch(userId, mapped, mode);
    }
}