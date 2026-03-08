package com.chip.board.solvedac.application.service;

import com.chip.board.baselinesync.infrastructure.persistence.dto.SolvedProblemItem;
import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.base.exception.ServiceException;
import com.chip.board.register.domain.User;
import com.chip.board.register.infrastructure.persistence.repository.UserRepository;
import com.chip.board.solvedac.application.port.api.SolvedAcPort;
import com.chip.board.solvedac.infrastructure.api.dto.response.SolvedProblemPage;
import com.chip.board.solvedac.presentation.dto.request.SolvedAcRandomProblemsRequest;
import com.chip.board.solvedac.presentation.dto.response.SolvedAcRandomProblemsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolvedAcRandomProblemService {

    private final SolvedAcPort solvedAcPort;
    private final UserRepository userRepository;

    public SolvedAcRandomProblemsResponse pick(Long userId, SolvedAcRandomProblemsRequest req) {
        String handle = null;

        if (req.solvedFilter() != SolvedAcRandomProblemsRequest.SolvedFilter.ANY) {
            handle = findBojHandleByUserIdOrThrow(userId);
        }

        String query = SolvedAcSearchQueryBuilder.build(
                req.algorithmTags(),
                req.minSolvedUserCount(),
                req.maxSolvedUserCount(),
                req.tierFrom(),
                req.tierTo(),
                req.solvedFilter(),
                handle
        );

        SolvedProblemPage page = solvedAcPort.searchProblemPageWithCountOrNull(query, 1, "random", "asc");

        if (page == null) {
            throw new ServiceException(ErrorCode.SOLVED_AC_UNAVAILABLE);
        }

        List<SolvedProblemItem> items = page.items();

        List<SolvedAcRandomProblemsResponse.Item> responseItems  = items.stream()
                .limit(req.limit())
                .map(it -> new SolvedAcRandomProblemsResponse.Item(
                        it.problemId(),
                        it.level(),
                        TierMapper.toTierCode(it.level()),
                        it.titleKo()
                ))
                .toList();

        return new SolvedAcRandomProblemsResponse(query, responseItems );
    }

    private String findBojHandleByUserIdOrThrow(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        String bojId = user.getBojId();
        if (bojId == null || bojId.isBlank()) {
            throw new ServiceException(ErrorCode.USER_BOJ_ID_NOT_SET);
        }
        return bojId;
    }
}