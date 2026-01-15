package com.chip.board.register.infrastructure.solved;

import com.chip.board.register.domain.UserSolvedSyncState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSolvedSyncStateRepository extends JpaRepository<UserSolvedSyncState, Long> {
}