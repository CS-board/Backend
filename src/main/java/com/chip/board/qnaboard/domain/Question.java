package com.chip.board.qnaboard.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "qna_question",
        indexes = {
                @Index(name = "idx_qna_question_created_at", columnList = "created_at"),
                @Index(name = "idx_qna_question_author", columnList = "author_id, created_at"),
                @Index(name = "idx_qna_question_deleted_created", columnList = "deleted, created_at")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 120, nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "author_name", length = 50, nullable = false)
    private String authorName;

    @Column(name = "solved", nullable = false)
    private boolean solved = false;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Question(String title, String content, Long authorId, String authorName) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void markSolved(boolean solved) { this.solved = solved; }
    public void softDelete() { this.deleted = true; }

    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
