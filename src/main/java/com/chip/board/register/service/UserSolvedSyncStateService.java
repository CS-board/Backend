package com.chip.board.register.service;

import com.chip.board.register.domain.User;
import com.chip.board.register.domain.UserSolvedSyncState;
import com.chip.board.register.repository.UserSolvedSyncStateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserSolvedSyncStateService {

    private final UserSolvedSyncStateRepository syncStateRepository;

    @Transactional
    public void createInitialSyncState(User savedUser) {
        // 유저당 1행 보장(중복 방지). 이미 있으면 그냥 리턴하거나 예외 처리
        if (syncStateRepository.existsById(savedUser.getId())) {
            return; // 또는 throw new IllegalStateException("sync state already exists");
        }

        UserSolvedSyncState state = UserSolvedSyncState.builder()
                .user(savedUser)
                .lastSyncAt(null)
                .build();

        syncStateRepository.save(state);
    }
}
