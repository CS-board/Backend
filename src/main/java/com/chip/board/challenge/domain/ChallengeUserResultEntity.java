package com.chip.board.challenge.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "challenge_user_result")
public class ChallengeUserResultEntity {

    @EmbeddedId
    private Pk pk;

    @Column(name = "solved_count", nullable = false)
    private Integer solvedCount;

    @Column(name = "total_points", nullable = false)
    private Long totalPoints;

    @Column(name = "last_rank_no")
    private Integer lastRankNo;

    @Column(name = "current_rank_no")
    private Integer currentRankNo;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Embeddable
    public static class Pk implements Serializable {

        @Column(name = "challenge_id", nullable = false)
        private Long challengeId;

        @Column(name = "user_id", nullable = false)
        private Long userId;
    }
}