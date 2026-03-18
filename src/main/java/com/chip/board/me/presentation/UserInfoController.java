package com.chip.board.me.presentation;

import com.chip.board.global.base.dto.ResponseBody;
import com.chip.board.global.base.dto.ResponseUtils;
import com.chip.board.global.jwt.annotation.CurrentUserId;
import com.chip.board.me.application.service.UserGoalService;
import com.chip.board.me.application.service.UserProfileService;
import com.chip.board.me.application.service.UserWithdrawalService;
import com.chip.board.me.presentation.dto.request.UpdateDepartmentRequest;
import com.chip.board.me.presentation.dto.request.UpdateGoalPointsRequest;
import com.chip.board.me.presentation.dto.request.UpdateGradeRequest;
import com.chip.board.me.presentation.dto.response.UserInfo.ProfileDetailResponse;
import com.chip.board.me.presentation.dto.response.UserInfo.UpdateDepartmentResponse;
import com.chip.board.me.presentation.dto.response.UserInfo.UpdateGradeResponse;
import com.chip.board.me.presentation.dto.response.UserInfo.ProfileResponse;
import com.chip.board.me.presentation.swagger.UserInfoSwagger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserInfoController implements UserInfoSwagger {

    private final UserGoalService userGoalService;
    private final UserProfileService userProfileService;
    private final UserWithdrawalService userWithdrawalService;

    @PatchMapping("/goal-points")
    public ResponseEntity<ResponseBody<Void>> updateGoalPoints(
            @CurrentUserId Long userId,
            @Valid @RequestBody UpdateGoalPointsRequest req
    ) {
        userGoalService.updateMyGoalPoints(userId, req.goalPoints());
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(null));
    }

    @GetMapping("/detail")
    public ResponseEntity<ResponseBody<ProfileDetailResponse>> getMyProfileDetail(
            @CurrentUserId Long userId
    ) {
        ProfileDetailResponse response = userProfileService.getMyProfileDetail(userId);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(response));
    }

    @GetMapping
    public ResponseEntity<ResponseBody<ProfileResponse>> getMyProfile(
            @CurrentUserId Long userId
    ) {
        ProfileResponse response = userProfileService.getMyProfile(userId);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(response));
    }

    @PatchMapping("/department")
    public ResponseEntity<ResponseBody<UpdateDepartmentResponse>> updateDepartment(
            @CurrentUserId Long userId,
            @Valid @RequestBody UpdateDepartmentRequest request
    ) {
        UpdateDepartmentResponse response =
                userProfileService.updateDepartment(userId, request.department());

        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(response));
    }

    @PatchMapping("/grade")
    public ResponseEntity<ResponseBody<UpdateGradeResponse>> updateGrade(
            @CurrentUserId Long userId,
            @Valid @RequestBody UpdateGradeRequest request
    ) {
        UpdateGradeResponse response =
                userProfileService.updateGrade(userId, request.grade());

        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(response));
    }

    @DeleteMapping
    public ResponseEntity<ResponseBody<Void>> withdraw(
            @CurrentUserId Long userId,
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        userWithdrawalService.withdraw(userId, refreshToken);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(null));
    }
}
