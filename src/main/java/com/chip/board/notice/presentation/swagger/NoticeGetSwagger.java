package com.chip.board.notice.presentation.swagger;

import com.chip.board.global.base.dto.ResponseBody;
import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.config.swagger.SwaggerApiFailedResponse;
import com.chip.board.global.config.swagger.SwaggerApiResponses;
import com.chip.board.global.config.swagger.SwaggerApiSuccessResponse;
import com.chip.board.notice.presentation.dto.response.BoardPostDetailResponse;
import com.chip.board.notice.presentation.dto.response.BoardPostListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Notice Post Query", description = "게시판 글(공지 포함) 조회 API")
public interface NoticeGetSwagger {

    @Operation(
            summary = "게시글 목록 조회",
            description = """
                    페이지네이션으로 게시글 목록을 조회합니다.
                    - 정렬: pinned DESC, createdAt DESC
                    """
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = BoardPostListResponse.class,
                    description = "게시글 목록 조회 성공"
            ),
            errors = {
                    // 없음
            }
    )
    ResponseEntity<ResponseBody<BoardPostListResponse>> list(
            @Parameter(
                    name = "page",
                    description = "페이지(0부터)",
                    required = false,
                    example = "0"
            )
            @Min(0) int page,

            @Parameter(
                    name = "size",
                    description = "페이지 크기(1~100)",
                    required = false,
                    example = "20"
            )
            @Min(1) @Max(100) int size
    );

    @Operation(
            summary = "게시글 상세 조회",
            description = "postId에 해당하는 게시글 상세 정보를 조회합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = BoardPostDetailResponse.class,
                    description = "게시글 상세 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ErrorCode.BOARD_POST_NOT_FOUND)
            }
    )
    ResponseEntity<ResponseBody<BoardPostDetailResponse>> detail(
            @Parameter(name = "postId", description = "게시글 ID", required = true, example = "1")
            @PathVariable("postId") Long postId
    );
}