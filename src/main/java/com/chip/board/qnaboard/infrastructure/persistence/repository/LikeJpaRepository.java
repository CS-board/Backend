package com.chip.board.qnaboard.infrastructure.persistence.repository;

import com.chip.board.qnaboard.domain.QuestionLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeJpaRepository extends JpaRepository<QuestionLike, Long> {
    Optional<QuestionLike> findByQuestionIdAndUserId(long questionId, long userId);
    long countByQuestionId(long questionId);
    void deleteByQuestionIdAndUserId(long questionId, long userId);
}

