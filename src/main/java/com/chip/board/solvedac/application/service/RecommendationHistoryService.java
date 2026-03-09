package com.chip.board.solvedac.application.service;

import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.base.exception.ServiceException;
import com.chip.board.register.application.port.UserRepositoryPort;
import com.chip.board.register.domain.User;
import com.chip.board.solvedac.application.port.SavedProblemPort;
import com.chip.board.solvedac.domain.SavedProblem;
import com.chip.board.solvedac.presentation.dto.request.SaveProblemsRequest;
import com.chip.board.solvedac.presentation.dto.response.RecommendationHistoryResponse;
import com.chip.board.solvedac.presentation.dto.response.SaveProblemsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationHistoryService {

    private static final int MAX_COUNT = 300;

    private final SavedProblemPort savedProblemPort;
    private final UserRepositoryPort userRepositoryPort;

    /**
     * 추천 기록 일괄 저장 (1~50개)
     * - 요청 내 problemId 중복 제거
     * - 이미 저장된 문제는 skip(멱등)
     * - 300개 초과분은 oldest부터 삭제
     */
    @Transactional
    public SaveProblemsResponse saveBatch(Long userId, SaveProblemsRequest req) {

        // 1) 요청 중복 제거 (problemId 기준)
        LinkedHashMap<Integer, SaveProblemsRequest.Item> unique = new LinkedHashMap<>();
        for (SaveProblemsRequest.Item it : req.items()) {
            unique.putIfAbsent(it.problemId(), it);
        }
        List<SaveProblemsRequest.Item> requested = new ArrayList<>(unique.values());

        // 2) 이미 저장된 problemId 조회 -> 제외
        Set<Integer> requestedIds = requested.stream()
                .map(SaveProblemsRequest.Item::problemId)
                .collect(Collectors.toSet());

        Set<Integer> existingIds = savedProblemPort.findByUserIdAndProblemIdIn(userId, requestedIds).stream()
                .map(SavedProblem::getProblemId)
                .collect(Collectors.toSet());

        List<SaveProblemsRequest.Item> toInsert = requested.stream()
                .filter(it -> !existingIds.contains(it.problemId()))
                .toList();

        int skippedCount = requested.size() - toInsert.size();

        // 3) 300개 제한 처리: (현재 + insert예정) 초과분만큼 oldest 삭제
        long currentCount = savedProblemPort.countByUserId(userId);
        int overflow = (int) Math.max(0, (currentCount + toInsert.size()) - MAX_COUNT);

        int evictedCount = 0;
        if (overflow > 0) {
            List<Long> oldestIds = savedProblemPort
                    .findOldestByUserId(userId, PageRequest.of(0, overflow))
                    .stream()
                    .map(SavedProblem::getId)
                    .toList();

            if (!oldestIds.isEmpty()) {
                savedProblemPort.deleteAllByIdInBatch(oldestIds);
                evictedCount = oldestIds.size();
            }
        }

        // 4) FK(User) 참조 확보
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        // 5) 저장
        int savedCount = 0;
        List<SaveProblemsResponse.Item> savedItems = new ArrayList<>();

        for (SaveProblemsRequest.Item it : toInsert) {
            try {
                SavedProblem saved = savedProblemPort.save(
                        new SavedProblem(user, it.problemId(), it.titleKo(), it.level())
                );
                savedCount++;
                savedItems.add(toSaveItem(saved));
            } catch (DataIntegrityViolationException e) {
                // 동시에 저장 눌렀을 때(유니크 제약) -> skip 처리
                skippedCount++;
            }
        }

        return new SaveProblemsResponse(savedCount, skippedCount, evictedCount, savedItems);
    }

    @Transactional(readOnly = true)
    public RecommendationHistoryResponse list(Long userId) {
        long count = savedProblemPort.countByUserId(userId);

        Pageable pageable = PageRequest.of(0, MAX_COUNT); // 0페이지에서 MAX_COUNT개
        List<RecommendationHistoryResponse.Item> items = savedProblemPort
                .findLatestByUserId(userId, pageable)
                .stream()
                .map(this::toHistoryItem)
                .toList();

        return new RecommendationHistoryResponse(count, MAX_COUNT, items);
    }

    @Transactional
    public void deleteOne(Long userId, Long savedId) {
        SavedProblem saved = savedProblemPort.findByIdAndUserId(savedId, userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.SAVED_PROBLEM_NOT_FOUND));

        savedProblemPort.delete(saved);
    }

    @Transactional
    public long clear(Long userId) {
        return savedProblemPort.deleteByUserId(userId);
    }

    private RecommendationHistoryResponse.Item toHistoryItem(SavedProblem e) {
        int level = (e.getLevel() == null) ? 0 : e.getLevel();
        return new RecommendationHistoryResponse.Item(
                e.getId(),
                e.getProblemId(),
                e.getTitleKo(),
                e.getLevel(),
                TierMapper.toTierCode(level),
                e.getCreatedAt()
        );
    }

    private SaveProblemsResponse.Item toSaveItem(SavedProblem e) {
        int level = (e.getLevel() == null) ? 0 : e.getLevel();
        return new SaveProblemsResponse.Item(
                e.getId(),
                e.getProblemId(),
                e.getTitleKo(),
                e.getLevel(),
                TierMapper.toTierCode(level),
                e.getCreatedAt()
        );
    }
}