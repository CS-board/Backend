package com.chip.board.register.application.port;

import com.chip.board.register.domain.User;

public interface UserSolvedSyncPort {
    void createInitialSyncState(User user);
}