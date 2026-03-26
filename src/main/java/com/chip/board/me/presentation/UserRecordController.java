package com.chip.board.me.presentation;

import com.chip.board.global.base.dto.ResponseBody;
import com.chip.board.global.base.dto.ResponseUtils;
import com.chip.board.global.jwt.annotation.CurrentUserId;
import com.chip.board.me.application.service.SolvedProblemQueryService;
import com.chip.board.me.application.service.MyRecordQueryService;
import com.chip.board.me.presentation.dto.response.UserRecord.SolvedProblemsResponse;
import com.chip.board.me.presentation.dto.response.UserRecord.MyChallengeSummaryResponse;
import com.chip.board.me.presentation.dto.response.UserRecord.MyRecordSummaryResponse;
import com.chip.board.me.presentation.dto.response.UserRecord.MyRecordWeeksResponse;
import com.chip.board.me.presentation.swagger.UserRecordSwagger;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/records")
public class UserRecordController implements UserRecordSwagger {

    private final SolvedProblemQueryService SolvedProblemQueryService;
    private final MyRecordQueryService myRecordQueryService;

    @GetMapping("/{challengeId}/solved-problems")
    public ResponseEntity<ResponseBody<List<SolvedProblemsResponse>>> getSolvedProblems(
            @CurrentUserId Long userId,
            @PathVariable Long challengeId
    ) {
        List<SolvedProblemsResponse> res = SolvedProblemQueryService.getSolvedProblems(userId, challengeId);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(res));
    }

    @GetMapping("/summary")
    public ResponseEntity<ResponseBody<MyRecordSummaryResponse>> summary(
            @CurrentUserId Long userId
    ) {
        MyRecordSummaryResponse res = myRecordQueryService.getMyRecordSummary(userId);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(res));
    }

    @GetMapping("/weeks")
    public ResponseEntity<ResponseBody<MyRecordWeeksResponse>> weeks(
            @CurrentUserId Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        MyRecordWeeksResponse res = myRecordQueryService.getWeeksSummary(userId, page, size);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(res));
    }

    @GetMapping("/{challengeId}/progress-summary")
    public ResponseEntity<ResponseBody<MyChallengeSummaryResponse>> myChallengeProgressSummary(
            @PathVariable Long challengeId,
            @CurrentUserId Long userId
    ) {
        MyChallengeSummaryResponse data = myRecordQueryService.getMyChallengeProgressSummary(challengeId, userId);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(data));
    }
}