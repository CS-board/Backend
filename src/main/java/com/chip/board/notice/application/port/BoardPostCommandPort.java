package com.chip.board.notice.application.port;

import com.chip.board.notice.domain.BoardPost;

public interface BoardPostCommandPort {
    Long save(BoardPost post);
    void update(Long id, String title, String content, boolean pinned);
    void delete(Long id);
}
