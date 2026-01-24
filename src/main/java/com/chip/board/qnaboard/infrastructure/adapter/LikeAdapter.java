package com.chip.board.qnaboard.infrastructure.adapter;

import com.chip.board.qnaboard.application.port.LikePort;
import com.chip.board.qnaboard.domain.QuestionLike;
import com.chip.board.qnaboard.infrastructure.persistence.repository.LikeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LikeAdapter implements LikePort {

    private final LikeJpaRepository likeJpaRepository;

    @Override
    @Transactional
    public boolean toggleLike(long questionId, long userId) {
        Optional<QuestionLike> existing = likeJpaRepository.findByQuestionIdAndUserId(questionId, userId);
        if (existing.isPresent()) {
            likeJpaRepository.delete(existing.get());
            return false;
        }
        likeJpaRepository.save(new QuestionLike(questionId, userId));
        return true;
    }

    @Override
    public long countByQuestionId(long questionId) {
        return likeJpaRepository.countByQuestionId(questionId);
    }
}

