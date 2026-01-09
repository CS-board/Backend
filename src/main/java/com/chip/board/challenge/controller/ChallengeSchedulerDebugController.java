package com.chip.board.challenge.controller;

import com.chip.board.challenge.servcie.ChallengeStatusScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/challenges")
class ChallengeSchedulerDebugController {

    private final ChallengeStatusScheduler scheduler;

    @PostMapping("/run-scheduler")
    public void runOnce() {
        scheduler.updateChallengeStatus();
    }
}
