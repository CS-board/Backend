package com.chip.board.score.infrastructure.adapter;

import com.chip.board.score.application.port.ChallengeUserResultPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChallengeUserResultJdbcAdapter implements ChallengeUserResultPort {

    private final JdbcTemplate jdbc;

    @Override
    public void refreshStatsForChallenge(long challengeId) {
        // 랭킹 컬럼(last/current)은 건드리지 않음 (00시 배치에서만 변경)
        jdbc.update("""
            INSERT INTO challenge_user_result (challenge_id, user_id, solved_count, total_points)
            SELECT
                se.challenge_id,
                se.user_id,
                COUNT(*) AS solved_count,
                COALESCE(SUM(se.points), 0) AS total_points
            FROM score_event se
            WHERE se.challenge_id = ?
            GROUP BY se.challenge_id, se.user_id
            ON DUPLICATE KEY UPDATE
                solved_count = VALUES(solved_count),
                total_points = VALUES(total_points),
                updated_at   = CURRENT_TIMESTAMP(6)
            """, challengeId);
    }

    @Override
    public void rolloverAndRecalculateRanks(long challengeId) {
        // 1) last <- current
        jdbc.update("""
            UPDATE challenge_user_result
            SET last_rank_no = current_rank_no
            WHERE challenge_id = ?
            """, challengeId);

        // 2) current 재계산 (MySQL 8+)
        jdbc.update("""
            UPDATE challenge_user_result cur
            JOIN (
                SELECT user_id,
                       DENSE_RANK() OVER (ORDER BY total_points DESC, solved_count DESC, user_id ASC) AS r
                FROM challenge_user_result
                WHERE challenge_id = ?
            ) x ON x.user_id = cur.user_id
            SET cur.current_rank_no = x.r,
                cur.updated_at = CURRENT_TIMESTAMP(6)
            WHERE cur.challenge_id = ?
            """, challengeId, challengeId);
    }
}