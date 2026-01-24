package com.chip.board.qnaboard.application.port;

import com.chip.board.qnaboard.domain.QuestionComment;

import java.util.List;

public interface CommentPort {
    QuestionComment save(QuestionComment comment);
    List<QuestionComment> findByQuestionId(long questionId);
    long countByQuestionId(long questionId);
}

