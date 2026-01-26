package com.chip.board.qnaboard.application.service;

import com.chip.board.qnaboard.presentation.dto.response.question.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentFacade {

    private final CommentService commentService;

    @Transactional
    public IdResponse addComment(long questionId, long userId, String content) {
        long cid = commentService.addComment(questionId, userId, content);
        return new IdResponse(cid);
    }

    @Transactional
    public void updateComment(long questionId, long commentId, String content, long requesterId) {
        commentService.updateComment(questionId, commentId, content, requesterId);
    }

    @Transactional
    public void deleteComment(long questionId, long commentId, long requesterId) {
        commentService.deleteComment(questionId, commentId, requesterId);
    }
}
