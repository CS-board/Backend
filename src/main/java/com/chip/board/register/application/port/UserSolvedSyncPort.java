package com.chip.board.register.application.port;

import com.chip.board.register.domain.User;
import com.chip.board.register.domain.UserSolvedSyncState;

public interface UserSolvedSyncPort {
    void createInitialSyncState(User user);

    boolean existsById(Long userId);

    void save(UserSolvedSyncState state);
}