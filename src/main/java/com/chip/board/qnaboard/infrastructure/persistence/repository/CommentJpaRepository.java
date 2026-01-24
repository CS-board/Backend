package com.chip.board.qnaboard.infrastructure.persistence.repository;

import com.chip.board.qnaboard.domain.QuestionComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentJpaRepository extends JpaRepository<QuestionComment, Long> {
    List<QuestionComment> findByQuestionIdAndDeletedFalseOrderByCreatedAtAsc(long questionId);
    long countByQuestionIdAndDeletedFalse(long questionId);
    Optional<QuestionComment> findByIdAndDeletedFalse(long id);
}
