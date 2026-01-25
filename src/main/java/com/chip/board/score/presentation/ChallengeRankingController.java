package com.chip.board.score.presentation;

import com.chip.board.score.application.service.ChallengeRankingQueryService;
import com.chip.board.score.presentation.dto.response.ChallengeRankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges")
public class ChallengeRankingController {

    private final ChallengeRankingQueryService challengeRankingQueryService;

    @GetMapping("/{challengeId}/rankings")
    public ResponseEntity<ChallengeRankingResponse> getRankings(
            @PathVariable long challengeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        return ResponseEntity.ok(challengeRankingQueryService.getRankingsAllUsers(challengeId, page, size));
    }
}
