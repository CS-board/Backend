package com.chip.board.notice.presentation.swagger;

import com.chip.board.global.base.dto.ResponseBody;
import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.config.swagger.SwaggerApiFailedResponse;
import com.chip.board.global.config.swagger.SwaggerApiResponses;
import com.chip.board.global.config.swagger.SwaggerApiSuccessResponse;
import com.chip.board.notice.presentation.dto.request.CreateBoardPostRequest;
import com.chip.board.notice.presentation.dto.request.UpdateBoardPostRequest;
import com.chip.board.qnaboard.presentation.dto.response.question.IdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Admin Notice Post", description = "관리자 게시판 글(공지 포함) 관리 API")
public interface AdminNoticePostSwagger {

    @Operation(
            summary = "게시글 생성(관리자)",
            description = "관리자가 게시글을 생성합니다. (pinned=true 인 경우 상단 고정)"
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.CREATED,
                    response = IdResponse.class,
                    description = "게시글 생성 성공"
            ),
            errors = {
            }
    )
    ResponseEntity<ResponseBody<IdResponse>> create(
            @Parameter(description = "게시글 생성 요청", required = true)
            @Valid @RequestBody CreateBoardPostRequest req
    );

    @Operation(
            summary = "게시글 수정(관리자)",
            description = "관리자가 postId에 해당하는 게시글을 수정합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = Void.class,
                    description = "게시글 수정 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ErrorCode.BOARD_POST_NOT_FOUND)
            }
    )
    ResponseEntity<ResponseBody<Void>> update(
            @Parameter(name = "postId", description = "게시글 ID", required = true, example = "1")
            @PathVariable("postId") Long postId,

            @Parameter(description = "게시글 수정 요청", required = true)
            @Valid @RequestBody UpdateBoardPostRequest req
    );

    @Operation(
            summary = "게시글 삭제(관리자)",
            description = "관리자가 postId에 해당하는 게시글을 삭제합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = Void.class,
                    description = "게시글 삭제 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ErrorCode.BOARD_POST_NOT_FOUND)
            }
    )
    ResponseEntity<ResponseBody<Void>> delete(
            @Parameter(name = "postId", description = "게시글 ID", required = true, example = "1")
            @PathVariable("postId") Long postId
    );
}