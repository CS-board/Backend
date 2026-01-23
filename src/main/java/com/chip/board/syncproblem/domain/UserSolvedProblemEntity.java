package com.chip.board.syncproblem.domain;

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
@Table(name = "user_solved_problem")
public class UserSolvedProblemEntity {

    @EmbeddedId
    private Pk pk;

    @Column(name = "level")
    private Byte level; // TINYINT NULL (tier_score.level FK)

    @Column(name = "first_seen_at", nullable = false)
    private LocalDateTime firstSeenAt; // DATETIME(6)

    @Column(name = "credited_at")
    private LocalDateTime creditedAt; // DATETIME(6) NULL

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Embeddable
    public static class Pk implements Serializable {

        @Column(name = "user_id", nullable = false)
        private Long userId;

        @Column(name = "problem_id", nullable = false)
        private Integer problemId;
    }
}