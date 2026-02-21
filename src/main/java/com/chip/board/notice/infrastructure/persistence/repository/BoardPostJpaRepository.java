package com.chip.board.notice.infrastructure.persistence.repository;

import com.chip.board.notice.domain.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardPostJpaRepository extends JpaRepository<BoardPost, Long> {
    Optional<BoardPost> findById(Long id);
}