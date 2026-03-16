package com.chip.board.qnaboard.domain;

import com.chip.board.global.config.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "qna_comment",
        indexes = {
                @Index(name = "idx_qna_comment_question", columnList = "question_id, created_at"),
                @Index(name = "idx_qna_comment_author", columnList = "author_id, created_at")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionComment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "author_name", length = 50, nullable = false)
    private String authorName;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public QuestionComment(Long questionId, Long authorId, String authorName, String content) {
        this.questionId = questionId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.content = content;
    }

    public void softDelete() { this.deleted = true; }

    public void changeContent(String content) {
        this.content = content;
    }
}
