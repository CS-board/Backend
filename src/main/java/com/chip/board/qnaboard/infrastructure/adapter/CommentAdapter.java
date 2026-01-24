package com.chip.board.qnaboard.infrastructure.adapter;

import com.chip.board.qnaboard.application.port.CommentPort;
import com.chip.board.qnaboard.domain.QuestionComment;
import com.chip.board.qnaboard.infrastructure.persistence.repository.CommentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentAdapter implements CommentPort {

    private final CommentJpaRepository commentJpaRepository;

    @Override
    public QuestionComment save(QuestionComment comment) {
        return commentJpaRepository.save(comment);
    }

    @Override
    public List<QuestionComment> findByQuestionId(long questionId) {
        return commentJpaRepository.findByQuestionIdAndDeletedFalseOrderByCreatedAtAsc(questionId);
    }

    @Override
    public long countByQuestionId(long questionId) {
        return commentJpaRepository.countByQuestionIdAndDeletedFalse(questionId);
    }

    @Override
    public Optional<QuestionComment> findActiveById(long commentId) {
        return commentJpaRepository.findByIdAndDeletedFalse(commentId);
    }
}

