package com.chip.board.notice.application.service;

import com.chip.board.global.base.exception.ServiceException;
import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.notice.application.port.BoardPostCommandPort;
import com.chip.board.notice.application.port.BoardPostQueryPort;
import com.chip.board.notice.domain.BoardPost;
import com.chip.board.notice.presentation.dto.response.BoardPostDetailResponse;
import com.chip.board.notice.presentation.dto.response.BoardPostListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardPostService {

    private static final int NEW_DAYS = 7;

    private final BoardPostQueryPort queryPort;
    private final BoardPostCommandPort commandPort;

    public BoardPostListResponse list(Pageable pageable) {
        Page<BoardPost> page = queryPort.findList(pageable);
        return BoardPostListResponse.from(page, LocalDateTime.now(), NEW_DAYS);
    }

    public BoardPostDetailResponse detail(Long postId) {
        BoardPost boardPost = queryPort.findById(postId)
                .orElseThrow(() -> new ServiceException(ErrorCode.BOARD_POST_NOT_FOUND));
        return BoardPostDetailResponse.from(boardPost);
    }

    @Transactional
    public Long create( String title, String content, boolean pinned) {
        BoardPost boardPost = new BoardPost(title, content, pinned);
        return commandPort.save(boardPost);
    }

    @Transactional
    public void update(Long postId, String title, String content, boolean pinned) {
        queryPort.findById(postId)
                .orElseThrow(() -> new ServiceException(ErrorCode.BOARD_POST_NOT_FOUND));
        commandPort.update(postId, title, content, pinned);
    }

    @Transactional
    public void delete(Long postId) {
        queryPort.findById(postId)
                .orElseThrow(() -> new ServiceException(ErrorCode.BOARD_POST_NOT_FOUND));

        commandPort.delete(postId);
    }
}
