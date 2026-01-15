package com.chip.board.oauth.application.component.writer;

import com.chip.board.register.domain.User;
import com.chip.board.register.application.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserWriter {
    private final UserRepository userRepository;

    public void onLoginSuccess(User user) {
        user.onLoginSuccess();
    }
}
