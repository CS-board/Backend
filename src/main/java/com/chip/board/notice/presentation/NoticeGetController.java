package com.chip.board.notice.presentation;

import com.chip.board.global.base.dto.ResponseBody;
import com.chip.board.global.base.dto.ResponseUtils;
import com.chip.board.notice.application.service.BoardPostService;
import com.chip.board.notice.presentation.dto.response.BoardPostDetailResponse;
import com.chip.board.notice.presentation.dto.response.BoardPostListResponse;
import com.chip.board.notice.presentation.swagger.NoticeGetSwagger;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board/posts")
@Validated
@RequiredArgsConstructor
public class NoticeGetController implements NoticeGetSwagger {

    private final BoardPostService boardPostService;

    @GetMapping
    public ResponseEntity<ResponseBody<BoardPostListResponse>> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Order.desc("pinned"), Sort.Order.desc("createdAt"))
        );

        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(boardPostService.list(pageable)));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseBody<BoardPostDetailResponse>> detail(@PathVariable Long postId) {
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(boardPostService.detail(postId)));
    }
}