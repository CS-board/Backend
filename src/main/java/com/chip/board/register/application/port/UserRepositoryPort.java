package com.chip.board.register.application.port;

import com.chip.board.register.domain.User;
import com.chip.board.register.application.port.dto.ChallengeRankingRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepositoryPort {

    Optional<User> findActiveByUsername(String username);

    Optional<User> findById(Long id);

    boolean existsActiveByBojId(String bojId);

    User save(User user);

    Page<ChallengeRankingRow> findRankingsAllUsers(Long challengeId, Pageable pageable);

    long countByDeletedFalse();

    boolean existsActiveByStudentId(String studentId);
}