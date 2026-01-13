package com.chip.board.baselinesync.infra;

import com.chip.board.baselinesync.model.SolvedProblemItem;
import com.chip.board.baselinesync.service.CreditedAtMode;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserSolvedProblemJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void upsertBatch(long userId, List<SolvedProblemItem> items, CreditedAtMode mode) {
        if (items == null || items.isEmpty()) return;

        final String sql = switch (mode) {
            case SEAL_NOW -> """
                INSERT INTO user_solved_problem (user_id, problem_id, level, credited_at)
                VALUES (?, ?, ?, NOW(6))
                ON DUPLICATE KEY UPDATE
                    level = VALUES(level),
                    credited_at = COALESCE(credited_at, VALUES(credited_at))
            """;
            case SCOREABLE_NULL -> """
                INSERT INTO user_solved_problem (user_id, problem_id, level, credited_at)
                VALUES (?, ?, ?, NULL)
                ON DUPLICATE KEY UPDATE
                    level = VALUES(level)
            """;
        };

        jdbcTemplate.batchUpdate(sql, items, 500, (ps, item) -> {
            ps.setLong(1, userId);
            ps.setInt(2, item.problemId());
            ps.setObject(3, item.level()); // null 허용
        });
    }
}
