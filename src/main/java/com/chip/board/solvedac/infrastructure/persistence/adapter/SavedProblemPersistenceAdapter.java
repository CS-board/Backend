package com.chip.board.solvedac.infrastructure.persistence.adapter;

import com.chip.board.solvedac.application.port.SavedProblemPort;
import com.chip.board.solvedac.domain.SavedProblem;
import com.chip.board.solvedac.infrastructure.persistence.repository.SavedProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SavedProblemPersistenceAdapter implements SavedProblemPort {

    private final SavedProblemRepository savedProblemRepository;

    @Override
    public long countByUserId(Long userId) {
        return savedProblemRepository.countByUser_Id(userId);
    }

    @Override
    public Optional<SavedProblem> findByIdAndUserId(Long savedId, Long userId) {
        return savedProblemRepository.findByIdAndUser_Id(savedId, userId);
    }

    @Override
    public List<SavedProblem> findLatestByUserId(Long userId, Pageable pageable) {
        return savedProblemRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public List<SavedProblem> findOldestByUserId(Long userId, Pageable pageable) {
        return savedProblemRepository.findByUser_IdOrderByCreatedAtAsc(userId, pageable);
    }

    @Override
    public List<SavedProblem> findByUserIdAndProblemIdIn(Long userId, Collection<Integer> problemIds) {
        return savedProblemRepository.findByUser_IdAndProblemIdIn(userId, problemIds);
    }

    @Override
    public SavedProblem save(SavedProblem entity) {
        return savedProblemRepository.save(entity);
    }

    @Override
    public void delete(SavedProblem entity) {
        savedProblemRepository.delete(entity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        savedProblemRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public long deleteByUserId(Long userId) {
        return savedProblemRepository.deleteByUser_Id(userId);
    }
}