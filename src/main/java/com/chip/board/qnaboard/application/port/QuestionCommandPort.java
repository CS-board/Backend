package com.chip.board.qnaboard.application.port;

import com.chip.board.qnaboard.domain.Question;

public interface QuestionCommandPort {
    Question save(Question question);
    void markSolved(long questionId, boolean solved);

    void update(long questionId, String title, String content);
    void softDelete(long questionId);
}
