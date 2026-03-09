package com.chip.board.solvedac.domain;

import com.chip.board.global.config.base.BaseEntity;
import com.chip.board.register.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "saved_problem",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_saved_problem_user_problem", columnNames = {"user_id", "problem_id"})
        },
        indexes = {
                @Index(name = "idx_saved_problem_user_created", columnList = "user_id, created_at")
        }
)
public class SavedProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_saved_problem_user")
    )
    private User user;

    @Column(name = "problem_id", nullable = false)
    private Integer problemId;

    @Column(name = "title_ko", nullable = false, length = 255)
    private String titleKo;

    @Column(name = "level", nullable = false)
    private Integer level;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SavedProblem(User user, Integer problemId, String titleKo, Integer level) {
        this.user = user;
        this.problemId = problemId;
        this.titleKo = titleKo;
        this.level = level;
    }
}
