package com.chip.board.score.application.port;

public interface ScoreEventPort {
    int insertScoreEventsForUncredited(long challengeId, long userId);
    int fillCreditedAtFromScoreEvent(long userId);
}
