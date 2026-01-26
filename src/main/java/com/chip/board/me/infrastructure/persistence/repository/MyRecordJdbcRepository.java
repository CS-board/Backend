package com.chip.board.me.infrastructure.persistence.repository;

import com.chip.board.me.application.service.model.MyRecordSummary;
import com.chip.board.me.application.service.model.MyRecordWeek;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyRecordJdbcRepository {

    private final JdbcTemplate jdbc;

    public MyRecordSummary findSummary(long userId) {
        return jdbc.queryForObject("""
            SELECT
                COALESCE(MAX(cur.total_points), 0) AS max_points,
                COALESCE(SUM(cur.solved_count), 0) AS total_solved
            FROM challenge_user_result cur
            JOIN challenge c ON c.challenge_id = cur.challenge_id
            WHERE cur.user_id = ?
              AND c.status = 'CLOSED'
              AND c.close_finalized = TRUE
            """,
                (rs, rowNum) -> new MyRecordSummary(
                        rs.getLong("max_points"),
                        rs.getInt("total_solved")
                ),
                userId
        );
    }


    /**
     * size+1개를 받아와서 hasNext 판단용으로 사용
     */
    public List<MyRecordWeek> findWeeks(long userId, int limitPlusOne, int offset) {
        return jdbc.query("""
                SELECT
                    DATE_FORMAT(c.start_at, '%x-W%v') AS week,
                    cur.solved_count AS solved_count,
                    cur.total_points AS score,
                    cur.current_rank_no AS rank_no
                FROM challenge_user_result cur
                JOIN challenge c ON c.challenge_id = cur.challenge_id
                WHERE cur.user_id = ?
                  AND c.status = 'CLOSED'
                  AND c.close_finalized = TRUE
                ORDER BY c.start_at DESC
                LIMIT ? OFFSET ?
                """,
                (rs, rowNum) -> new MyRecordWeek(
                        rs.getString("week"),
                        rs.getInt("solved_count"),
                        rs.getLong("score"),
                        (Integer) rs.getObject("rank_no")
                ),
                userId, limitPlusOne, offset
        );
    }
}

