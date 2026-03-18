package com.chip.board.challenge.presentation.dto.response;

import com.chip.board.challenge.domain.Challenge;
import com.chip.board.challenge.domain.ChallengeStatus;

import java.time.LocalDateTime;

public record ChallengeListItemResponse(
        Long challengeId,
        String title,
        LocalDateTime startAt,
        LocalDateTime endAt,
        ChallengeStatus status
) {
    public static ChallengeListItemResponse from(Challenge challenge) {
        return new ChallengeListItemResponse(
                challenge.getChallengeId(),
                challenge.getTitle(),
                challenge.getStartAt(),
                challenge.getEndAt(),
                challenge.getStatus()
        );
    }
}