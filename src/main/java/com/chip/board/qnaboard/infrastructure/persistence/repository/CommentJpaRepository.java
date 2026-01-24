package com.chip.board.qnaboard.infrastructure.persistence.repository;

import com.chip.board.qnaboard.domain.QuestionComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<QuestionComment, Long> {
    List<QuestionComment> findByQuestionIdAndDeletedFalseOrderByCreatedAtAsc(long questionId);
    long countByQuestionIdAndDeletedFalse(long questionId);
}
