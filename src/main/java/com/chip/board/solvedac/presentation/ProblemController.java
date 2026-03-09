package com.chip.board.solvedac.presentation;

import com.chip.board.global.base.dto.ResponseBody;
import com.chip.board.global.base.dto.ResponseUtils;
import com.chip.board.global.jwt.annotation.CurrentUserId;
import com.chip.board.solvedac.application.service.RecommendationHistoryService;
import com.chip.board.solvedac.application.service.SolvedAcRandomProblemService;
import com.chip.board.solvedac.presentation.dto.request.SaveProblemsRequest;
import com.chip.board.solvedac.presentation.dto.request.SolvedAcRandomProblemsRequest;
import com.chip.board.solvedac.presentation.dto.response.RecommendationHistoryResponse;
import com.chip.board.solvedac.presentation.dto.response.SaveProblemsResponse;
import com.chip.board.solvedac.presentation.dto.response.SolvedAcRandomProblemsResponse;
import com.chip.board.solvedac.presentation.swagger.ProblemSwagger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
@RequestMapping("/api/problems")
public class ProblemController implements ProblemSwagger {

    private final SolvedAcRandomProblemService solvedAcRandomProblemService;
    private final RecommendationHistoryService recommendationHistoryService;

    @PostMapping("/random")
    public ResponseEntity<ResponseBody<SolvedAcRandomProblemsResponse>> random(
            @CurrentUserId Long userId,
            @Valid @RequestBody SolvedAcRandomProblemsRequest req
    ) {
        SolvedAcRandomProblemsResponse res = solvedAcRandomProblemService.pick(userId, req);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(res));
    }

    // 저장(추천 기록 추가)
    @PostMapping("/save")
    public ResponseEntity<ResponseBody<SaveProblemsResponse>> saveBatch(
            @CurrentUserId Long userId,
            @Valid @RequestBody SaveProblemsRequest req
    ) {
        SaveProblemsResponse res = recommendationHistoryService.saveBatch(userId, req);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(res));
    }

    // 조회(사진처럼 최신 300개 + count/maxCount)
    @GetMapping("/saved/list")
    public ResponseEntity<ResponseBody<RecommendationHistoryResponse>> list(
            @CurrentUserId Long userId
    ) {
        RecommendationHistoryResponse res = recommendationHistoryService.list(userId);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(res));
    }

    // 개별 삭제(휴지통 아이콘)
    @DeleteMapping("/saved/delete/{savedId}")
    public ResponseEntity<ResponseBody<Void>> deleteOne(
            @CurrentUserId Long userId,
            @PathVariable Long savedId
    ) {
        recommendationHistoryService.deleteOne(userId, savedId);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(null));
    }

    // 전체 비우기(추천 기록 비우기 버튼)
    @DeleteMapping("/saved/delete/all")
    public ResponseEntity<ResponseBody<Long>> clear(
            @CurrentUserId Long userId
    ) {
        long deletedCount = recommendationHistoryService.clear(userId);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(deletedCount));
    }
}