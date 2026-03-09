package com.chip.board.solvedac.infrastructure.persistence.repository;

import com.chip.board.solvedac.domain.SavedProblem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SavedProblemRepository extends JpaRepository<SavedProblem, Long> {

    long countByUser_Id(Long userId);

    List<SavedProblem> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<SavedProblem> findByUser_IdAndProblemIdIn(Long userId, Collection<Integer> problemIds);

    List<SavedProblem> findByUser_IdOrderByCreatedAtAsc(Long userId, Pageable pageable);

    Optional<SavedProblem> findByIdAndUser_Id(Long savedId, Long userId);

    long deleteByUser_Id(Long userId);
}