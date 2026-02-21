package com.chip.board.notice.presentation;

import com.chip.board.global.base.dto.ResponseBody;
import com.chip.board.global.base.dto.ResponseUtils;
import com.chip.board.notice.application.service.BoardPostService;
import com.chip.board.notice.presentation.dto.request.CreateBoardPostRequest;
import com.chip.board.notice.presentation.dto.request.UpdateBoardPostRequest;
import com.chip.board.notice.presentation.swagger.AdminNoticePostSwagger;
import com.chip.board.qnaboard.presentation.dto.response.question.IdResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/board/posts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
public class AdminNoticePostController implements AdminNoticePostSwagger {

    private final BoardPostService service;

    @PostMapping
    public ResponseEntity<ResponseBody<IdResponse>> create(@Valid @RequestBody CreateBoardPostRequest req) {
        Long id = service.create(req.title(), req.content(), req.pinned());
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtils.createSuccessResponse(new IdResponse(id)));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ResponseBody<Void>> update(
            @PathVariable Long postId,
            @Valid @RequestBody UpdateBoardPostRequest req
    ) {
        service.update(postId, req.title(), req.content(), req.pinned());
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(null));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseBody<Void>> delete(@PathVariable Long postId) {
        service.delete(postId);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(null));
    }
}
