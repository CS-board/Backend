package com.chip.board.register.application.port;

import java.time.Duration;

public interface VerificationCodeStore {
    void saveAuthCode(String email, int code, Duration ttl);
    String get(String email);
    void markVerified(String email, Duration ttl);
    void delete(String email);
}
