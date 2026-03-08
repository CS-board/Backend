package com.chip.board.solvedac.presentation;

import com.chip.board.global.base.dto.ResponseBody;
import com.chip.board.global.base.dto.ResponseUtils;
import com.chip.board.global.jwt.annotation.CurrentUserId;
import com.chip.board.solvedac.application.service.SolvedAcRandomProblemService;
import com.chip.board.solvedac.presentation.dto.request.SolvedAcRandomProblemsRequest;
import com.chip.board.solvedac.presentation.dto.response.SolvedAcRandomProblemsResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
@RequestMapping("/api/problems")
public class RandomProblemController {

    private final SolvedAcRandomProblemService service;

    @PostMapping("/random")
    public ResponseEntity<ResponseBody<SolvedAcRandomProblemsResponse>> random(
            @CurrentUserId Long userId,
            @Valid @RequestBody SolvedAcRandomProblemsRequest req
    ) {
        SolvedAcRandomProblemsResponse res = service.pick(userId, req);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(res));
    }
}