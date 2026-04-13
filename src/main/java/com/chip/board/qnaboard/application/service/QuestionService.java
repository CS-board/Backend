package com.chip.board.qnaboard.application.service;

import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.base.exception.ServiceException;
import com.chip.board.qnaboard.application.port.CommentPort;
import com.chip.board.qnaboard.application.port.LikePort;
import com.chip.board.qnaboard.application.port.QuestionCommandPort;
import com.chip.board.qnaboard.application.port.QuestionQueryPort;
import com.chip.board.qnaboard.domain.Question;
import com.chip.board.qnaboard.domain.QuestionComment;
import com.chip.board.qnaboard.infrastructure.persistence.dto.QuestionSummaryRow;
import com.chip.board.register.application.port.UserRepositoryPort;
import com.chip.board.register.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionQueryPort queryPort;
    private final QuestionCommandPort commandPort;
    private final CommentPort commentPort;
    private final LikePort likePort;
    private final UserRepositoryPort userRepositoryPort;

    @Transactional(readOnly = true)
    public Page<QuestionSummaryRow> list(Pageable pageable) {
        return queryPort.findSummaries(pageable);
    }

    @Transactional(readOnly = true)
    public QuestionQueryPort.QuestionDetailView getDetail(long questionId) {
        log.debug("QnA question detail requested. questionId={}", questionId);
        return queryPort.findDetail(questionId)
                .orElseThrow(() -> new ServiceException(ErrorCode.QNA_QUESTION_NOT_FOUND));
    }

    @Transactional
    public long create(String title, String content, long userId) {
        User user = getUserOrThrow(userId);
        Question saved = commandPort.save(new Question(title, content, userId, user.getName()));
        log.info("QnA question created. questionId={}, userId={}", saved.getId(), userId);
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public List<QuestionComment> listComments(long questionId) {
        validateQuestionExists(questionId);
        return commentPort.findByQuestionId(questionId);
    }

    @Transactional
    public LikePort.ToggleResult toggleLike(long questionId, long userId) {
        validateQuestionExists(questionId);
        LikePort.ToggleResult result = likePort.toggle(questionId, userId);
        log.debug("QnA question like toggled. questionId={}, userId={}", questionId, userId);
        return result;
    }

    @Transactional
    public void markSolved(long questionId, boolean solved, long requesterId) {
        QuestionQueryPort.QuestionDetailView view = getDetail(questionId);
        validateOwnerOrAdmin(view, requesterId);
        commandPort.markSolved(questionId, solved);
        log.info("QnA question solved status changed. questionId={}, userId={}", questionId, requesterId);
    }

    @Transactional
    public void update(long questionId, String title, String content, long requesterId) {
        QuestionQueryPort.QuestionDetailView view = getDetail(questionId);
        validateOwnerOrAdmin(view, requesterId);
        commandPort.update(questionId, title, content);
        log.info("QnA question updated. questionId={}, userId={}", questionId, requesterId);
    }

    @Transactional
    public void delete(long questionId, long requesterId) {
        QuestionQueryPort.QuestionDetailView view = getDetail(questionId);
        validateOwnerOrAdmin(view, requesterId);
        commandPort.softDelete(questionId);
        log.info("QnA question deleted. questionId={}, userId={}", questionId, requesterId);
    }

    private User getUserOrThrow(long userId) {
        return userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateOwnerOrAdmin(QuestionQueryPort.QuestionDetailView view, long requesterId) {
        if (view.authorId() == requesterId) {
            return;
        }

        User requester = userRepositoryPort.findById(requesterId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        if (requester.getRole() == com.chip.board.register.domain.Role.ADMIN) {
            return;
        }

        // 3) 나머지는 차단
        throw new ServiceException(ErrorCode.QNA_QUESTION_FORBIDDEN);
    }

    private void validateQuestionExists(long questionId) {
        if (!queryPort.existsActiveById(questionId)) {
            throw new ServiceException(ErrorCode.QNA_QUESTION_NOT_FOUND);
        }
    }
}
