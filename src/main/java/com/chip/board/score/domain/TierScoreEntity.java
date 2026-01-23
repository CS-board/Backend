package com.chip.board.score.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tier_score")
public class TierScoreEntity {

    @Id
    @Column(name = "level", nullable = false)
    private Byte level;

    @Column(name = "tier_name", nullable = false, length = 20, unique = true)
    private String tierName;

    @Column(name = "points", nullable = false)
    private Integer points;
}