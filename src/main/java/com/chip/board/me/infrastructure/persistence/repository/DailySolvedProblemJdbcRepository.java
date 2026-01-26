package com.chip.board.me.infrastructure.persistence.repository;

import com.chip.board.me.application.port.DailySolvedProblemQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DailySolvedProblemJdbcRepository {

    private final JdbcTemplate jdbc;

    public List<DailySolvedProblemQueryPort.Row> findByUserIdAndChallengeIdBetween(
            long userId,
            long challengeId,
            LocalDateTime startInclusive,
            LocalDateTime endExclusive
    ) {
        String sql = """
            SELECT
                se.problem_id AS problem_id,
                usp.title_ko  AS title_ko,
                se.level      AS level,
                ts.tier_name  AS tier_name,
                se.points     AS points,
                se.created_at AS solved_at
            FROM score_event se
            JOIN user_solved_problem usp
              ON usp.user_id = se.user_id
             AND usp.problem_id = se.problem_id
            JOIN tier_score ts
              ON ts.level = se.level
            WHERE se.user_id = ?
              AND se.challenge_id = ?
              AND se.created_at >= ?
              AND se.created_at < ?
            ORDER BY se.created_at DESC
            """;

        return jdbc.query(
                sql,
                (rs, rowNum) -> new DailySolvedProblemQueryPort.Row(
                        rs.getInt("problem_id"),
                        rs.getString("title_ko"),
                        rs.getInt("level"),
                        rs.getString("tier_name"),
                        rs.getInt("points"),
                        toInstant(rs.getTimestamp("solved_at"))
                ),
                userId,
                challengeId,
                Timestamp.valueOf(startInclusive),
                Timestamp.valueOf(endExclusive)
        );
    }

    private static Instant toInstant(Timestamp ts) {
        return ts == null ? null : ts.toInstant();
    }
}
