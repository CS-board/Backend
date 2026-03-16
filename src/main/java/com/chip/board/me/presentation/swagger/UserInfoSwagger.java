package com.chip.board.me.presentation.swagger;

import com.chip.board.global.base.dto.ResponseBody;
import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.config.swagger.SwaggerApiFailedResponse;
import com.chip.board.global.config.swagger.SwaggerApiResponses;
import com.chip.board.global.config.swagger.SwaggerApiSuccessResponse;
import com.chip.board.me.presentation.dto.request.UpdateDepartmentRequest;
import com.chip.board.me.presentation.dto.request.UpdateGoalPointsRequest;
import com.chip.board.me.presentation.dto.request.UpdateGradeRequest;
import com.chip.board.me.presentation.dto.response.ProfileDetailResponse;
import com.chip.board.me.presentation.dto.response.ProfileResponse;
import com.chip.board.me.presentation.dto.response.UpdateDepartmentResponse;
import com.chip.board.me.presentation.dto.response.UpdateGradeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Me User", description = "내 사용자 정보 API")
public interface UserInfoSwagger {

    @Operation(
            summary = "내 목표점수 변경",
            description = """
                    로그인한 사용자의 목표 점수를 변경합니다.
                    - goalPoints는 0 이상이어야 합니다.
                    """
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = Void.class,
                    description = "내 목표점수 변경 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ErrorCode.INVALID_GOAL_POINTS),
                    @SwaggerApiFailedResponse(ErrorCode.USER_NOT_FOUND)
            }
    )
    @PatchMapping("/goal-points")
    ResponseEntity<ResponseBody<Void>> updateGoalPoints(
            @Parameter(hidden = true)
            Long userId,

            @Parameter(description = "목표 점수 변경 요청", required = true)
            @Valid @RequestBody UpdateGoalPointsRequest req
    );

    @Operation(
            summary = "내 프로필 상세 조회",
            description = "로그인한 사용자의 프로필 상세 정보를 조회합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = ProfileDetailResponse.class,
                    description = "내 프로필 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ErrorCode.USER_NOT_FOUND)
            }
    )
    @GetMapping("/detail")
    ResponseEntity<ResponseBody<ProfileDetailResponse>> getMyProfileDetail(
            @Parameter(hidden = true)
            Long userId
    );

    @Operation(
            summary = "내 프로필 조회(이름, 학과)",
            description = "로그인한 사용자의 프로필 정보를 조회합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = ProfileResponse.class,
                    description = "내 프로필 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ErrorCode.USER_NOT_FOUND)
            }
    )
    @GetMapping
    ResponseEntity<ResponseBody<ProfileResponse>> getMyProfile(
            @Parameter(hidden = true)
            Long userId
    );



    @Operation(
            summary = "내 학과 변경",
            description = "로그인한 사용자의 학과를 변경하고 변경된 값을 반환합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = UpdateDepartmentResponse.class,
                    description = "내 학과 변경 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ErrorCode.INVALID_DEPARTMENT),
                    @SwaggerApiFailedResponse(ErrorCode.USER_NOT_FOUND)
            }
    )
    @PatchMapping("/department")
    ResponseEntity<ResponseBody<UpdateDepartmentResponse>> updateDepartment(
            @Parameter(hidden = true)
            Long userId,

            @Parameter(description = "학과 변경 요청", required = true)
            @Valid @RequestBody UpdateDepartmentRequest request
    );

    @Operation(
            summary = "내 학년 변경",
            description = "로그인한 사용자의 학년을 변경하고 변경된 값을 반환합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    status = HttpStatus.OK,
                    response = UpdateGradeResponse.class,
                    description = "내 학년 변경 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ErrorCode.INVALID_USER_GRADE),
                    @SwaggerApiFailedResponse(ErrorCode.USER_NOT_FOUND)
            }
    )
    @PatchMapping("/grade")
    ResponseEntity<ResponseBody<UpdateGradeResponse>> updateGrade(
            @Parameter(hidden = true)
            Long userId,

            @Parameter(description = "학년 변경 요청", required = true)
            @Valid @RequestBody UpdateGradeRequest request
    );
}