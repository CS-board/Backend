package com.chip.board.score.application.port;

public interface ChallengeUserResultPort {

    void refreshStatsForChallenge(long challengeId);

    void rolloverAndRecalculateRanks(long challengeId);
}
