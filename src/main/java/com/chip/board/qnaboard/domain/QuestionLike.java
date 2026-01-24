package com.chip.board.qnaboard.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "qna_like",
        uniqueConstraints = @UniqueConstraint(name = "uk_qna_like", columnNames = {"question_id", "user_id"}),
        indexes = @Index(name = "idx_qna_like_question", columnList = "question_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public QuestionLike(Long questionId, Long userId) {
        this.questionId = questionId;
        this.userId = userId;
    }

    @PrePersist
    void prePersist() { this.createdAt = LocalDateTime.now(); }
}
