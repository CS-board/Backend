package com.chip.board.challenge.presentation;

import com.chip.board.challenge.presentation.dto.request.ChallengeCreateRequest;
import com.chip.board.challenge.presentation.dto.response.ChallengeInfoResponse;
import com.chip.board.challenge.application.service.ChallengeCommandService;
import com.chip.board.challenge.presentation.dto.response.ChallengeDetailInfoResponse;
import com.chip.board.challenge.presentation.dto.response.ChallengeListItemResponse;
import com.chip.board.challenge.presentation.swagger.ChallengeSwagger;
import com.chip.board.global.base.dto.ResponseBody;
import com.chip.board.global.base.dto.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/challenges")
public class ChallengeController implements ChallengeSwagger {

    private final ChallengeCommandService challengeCommandService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/hold")
    public ResponseEntity<ResponseBody<ChallengeInfoResponse>> hold(@RequestBody @Valid ChallengeCreateRequest request) {
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse( challengeCommandService.hold(request)));
    }

    @GetMapping
    public ResponseEntity<ResponseBody<List<ChallengeListItemResponse>>> getChallenges() {
        List<ChallengeListItemResponse> data = challengeCommandService.getChallengeList();
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(data));
    }

    @GetMapping("/{challengeId}/details")
    public ResponseEntity<ResponseBody<ChallengeDetailInfoResponse>> detailInfo(
            @PathVariable("challengeId") Long challengeId
    ) {
        ChallengeDetailInfoResponse data = challengeCommandService.getDetailInfo(challengeId);
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(data));
    }

}
