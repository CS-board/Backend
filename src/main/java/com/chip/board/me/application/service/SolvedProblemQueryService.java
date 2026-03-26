package com.chip.board.me.application.service;

import com.chip.board.challenge.application.port.ChallengeLoadPort;
import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.base.exception.ServiceException;
import com.chip.board.me.application.port.SolvedProblemQueryPort;
import com.chip.board.me.presentation.dto.response.UserRecord.SolvedProblemsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolvedProblemQueryService {

    private final ChallengeLoadPort challengeLoadPort;
    private final SolvedProblemQueryPort solvedProblemQueryPort;
    private final Clock clock;

    public List<SolvedProblemsResponse> getSolvedProblems(long userId, long challengeId) {
        if (!challengeLoadPort.existsById(challengeId)) {
            throw new ServiceException(ErrorCode.CHALLENGE_NOT_FOUND);
        }

        List<SolvedProblemQueryPort.Row> rows =
                solvedProblemQueryPort.findByUserIdAndChallengeId(userId, challengeId);

        Map<LocalDate, List<SolvedProblemQueryPort.Row>> grouped = rows.stream()
                .collect(Collectors.groupingBy(
                        row -> row.solvedAt().atZone(clock.getZone()).toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return grouped.entrySet().stream()
                .map(entry -> new SolvedProblemsResponse(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream()
                                .map(row -> new SolvedProblemsResponse.Item(
                                        row.problemId(),
                                        row.titleKo(),
                                        row.level(),
                                        row.tierName(),
                                        row.points(),
                                        row.solvedAt()
                                ))
                                .toList()
                ))
                .toList();
    }
}