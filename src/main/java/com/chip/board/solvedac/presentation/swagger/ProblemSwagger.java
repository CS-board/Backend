package com.chip.board.solvedac.presentation.swagger;

import com.chip.board.global.base.dto.ResponseBody;
import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.config.swagger.SwaggerApiFailedResponse;
import com.chip.board.global.config.swagger.SwaggerApiResponses;
import com.chip.board.global.config.swagger.SwaggerApiSuccessResponse;
import com.chip.board.solvedac.presentation.dto.request.SaveProblemsRequest;
import com.chip.board.solvedac.presentation.dto.request.SolvedAcRandomProblemsRequest;
import com.chip.board.solvedac.presentation.dto.response.RecommendationHistoryResponse;
import com.chip.board.solvedac.presentation.dto.response.SaveProblemsResponse;
import com.chip.board.solvedac.presentation.dto.response.SolvedAcRandomProblemsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Problem", description = "랜덤 문제 추천/추천 기록 API")
public interface ProblemSwagger {

    @Operation(
            summary = "랜덤 문제 추천",
            description = """
                    solved.ac 조건으로 랜덤 문제를 추천합니다.
                    - algorithmTags, 티어 범위, 맞은 사람 수, 푼/안 푼 필터, 생성 제한 개수limit(1~50)을 사용합니다.
                    - solved.ac 쿨다운(429) 중이면 제한됩니다.
                    """
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = SolvedAcRandomProblemsResponse.class,
                    description = "랜덤 문제 추천 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ErrorCode.USER_BOJ_ID_NOT_SET),
                    @SwaggerApiFailedResponse(ErrorCode.SOLVED_AC_UNAVAILABLE),
                    @SwaggerApiFailedResponse(ErrorCode.USER_NOT_FOUND)
            }
    )
    ResponseEntity<ResponseBody<SolvedAcRandomProblemsResponse>> random(
            @Parameter(hidden = true) Long userId,
            SolvedAcRandomProblemsRequest req
    );

    @Operation(
            summary = "추천 기록 저장(일괄)",
            description = """
                    랜덤으로 추천한 문제 목록(1~50개)을 추천 기록에 저장합니다.
                    - 동일한 problemId는 멱등 처리(이미 저장된 것은 건너뜀)합니다.
                    - 최대 300개 제한이 있으면 초과분은 오래된 기록부터 정리될 수 있습니다.
                    """
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = SaveProblemsResponse.class,
                    description = "추천 기록 저장 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ErrorCode.USER_NOT_FOUND),
            }
    )
    ResponseEntity<ResponseBody<SaveProblemsResponse>> saveBatch(
            @Parameter(hidden = true) Long userId,
            SaveProblemsRequest req
    );

    @Operation(
            summary = "추천 기록 조회",
            description = "추천 기록을 최신순으로 조회합니다. (최대 300개 + count/maxCount 포함)"
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = RecommendationHistoryResponse.class,
                    description = "추천 기록 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ErrorCode.USER_NOT_FOUND)
            }
    )
    ResponseEntity<ResponseBody<RecommendationHistoryResponse>> list(
            @Parameter(hidden = true) Long userId
    );

    @Operation(
            summary = "추천 기록 개별 삭제",
            description = "savedId로 추천 기록 1개를 삭제합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = Void.class,
                    description = "추천 기록 개별 삭제 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ErrorCode.INVALID_ENDPOINT),
                    @SwaggerApiFailedResponse(ErrorCode.SAVED_PROBLEM_NOT_FOUND)
            }
    )
    ResponseEntity<ResponseBody<Void>> deleteOne(
            @Parameter(hidden = true) Long userId,
            @Parameter(name = "savedId", description = "추천 기록 ID", required = true, example = "1")
            @PathVariable Long savedId
    );

    @Operation(
            summary = "추천 기록 전체 비우기",
            description = "해당 유저의 추천 기록을 모두 삭제합니다. (삭제된 개수 반환)"
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = Long.class,
                    description = "추천 기록 전체 삭제 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ErrorCode.USER_NOT_FOUND),

            }
    )
    ResponseEntity<ResponseBody<Long>> clear(
            @Parameter(hidden = true) Long userId
    );
}
