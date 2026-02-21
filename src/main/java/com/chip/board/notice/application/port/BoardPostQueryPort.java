package com.chip.board.notice.application.port;

import com.chip.board.notice.domain.BoardPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BoardPostQueryPort {
    Page<BoardPost> findList(Pageable pageable);
    Optional<BoardPost> findById(Long id);
}