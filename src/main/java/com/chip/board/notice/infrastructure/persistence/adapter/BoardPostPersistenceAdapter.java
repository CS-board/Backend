package com.chip.board.notice.infrastructure.persistence.adapter;

import com.chip.board.notice.application.port.BoardPostCommandPort;
import com.chip.board.notice.application.port.BoardPostQueryPort;
import com.chip.board.notice.domain.BoardPost;
import com.chip.board.notice.infrastructure.persistence.repository.BoardPostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BoardPostPersistenceAdapter implements BoardPostQueryPort, BoardPostCommandPort {

    private final BoardPostJpaRepository boardPostJpaRepository;

    @Override
    public Page<BoardPost> findList(Pageable pageable) {
        return boardPostJpaRepository.findAll(pageable);
    }


    @Override
    public Optional<BoardPost> findById(Long id) {
        return boardPostJpaRepository.findById(id);
    }

    @Override
    public Long save(BoardPost post) {
        return boardPostJpaRepository.save(post).getId();
    }

    @Override
    public void update(Long id, String title, String content, boolean pinned) {
        BoardPost boardPost = boardPostJpaRepository.findById(id).orElseThrow();
        boardPost.setTitle(title);
        boardPost.setContent(content);
        boardPost.setPinned(pinned);
        boardPostJpaRepository.save(boardPost);
    }

    @Override
    public void delete(Long id) {
        BoardPost boardPost = boardPostJpaRepository.findById(id).orElseThrow();
        boardPostJpaRepository.delete(boardPost);
    }
}
