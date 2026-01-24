package com.chip.board.qnaboard.application.port;

public interface LikePort {
    boolean toggleLike(long questionId, long userId); // true=liked, false=unliked
    long countByQuestionId(long questionId);
}
