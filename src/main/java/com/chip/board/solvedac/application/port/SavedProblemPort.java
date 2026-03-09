package com.chip.board.solvedac.application.port;

import com.chip.board.solvedac.domain.SavedProblem;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SavedProblemPort {

    long countByUserId(Long userId);

    Optional<SavedProblem> findByIdAndUserId(Long savedId, Long userId);

    List<SavedProblem> findLatestByUserId(Long userId, Pageable pageable);

    List<SavedProblem> findOldestByUserId(Long userId, Pageable pageable);

    List<SavedProblem> findByUserIdAndProblemIdIn(Long userId, Collection<Integer> problemIds);

    SavedProblem save(SavedProblem entity);

    void delete(SavedProblem entity);

    void deleteAllByIdInBatch(List<Long> ids);

    long deleteByUserId(Long userId);
}