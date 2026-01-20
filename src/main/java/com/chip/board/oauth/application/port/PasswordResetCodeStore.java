package com.chip.board.oauth.application.port;

import java.time.Duration;

public interface PasswordResetCodeStore {
    String VERIFIED_STATE = "VERIFIED";

    void saveAuthCode(String email, int code, Duration ttl);
    String get(String email);
    void markVerified(String email, Duration ttl);
    void delete(String email);
}
