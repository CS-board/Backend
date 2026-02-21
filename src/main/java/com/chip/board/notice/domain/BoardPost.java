package com.chip.board.notice.domain;

import com.chip.board.global.config.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "board_post")
@SQLDelete(sql = "UPDATE board_post SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class BoardPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "is_pinned", nullable = false)
    private boolean pinned;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Builder
    public BoardPost(String title, String content, boolean pinned) {
        this.title = title;
        this.content = content;
        this.pinned = pinned;
    }

    public void update(String title, String content, boolean pinned) {
        this.title = title;
        this.content = content;
        this.pinned = pinned;
    }
}