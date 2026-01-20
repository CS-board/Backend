package com.chip.board.oauth.application.service;

import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.base.exception.ServiceException;
import com.chip.board.oauth.application.port.PasswordResetCodeStore;
import com.chip.board.register.application.port.EmailSender;
import com.chip.board.register.application.port.UserRepositoryPort;
import com.chip.board.register.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetCodeStore passwordResetCodeStore;
    private final EmailSender emailSender;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Value("${verification.code.expiry-minutes}")
    private int expiryMinutes;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public void sendResetCode(String email) {
        User user = userRepositoryPort.findByUsername(email)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        int code = SECURE_RANDOM.nextInt(900000) + 100000;
        passwordResetCodeStore.saveAuthCode(email, code, Duration.ofMinutes(expiryMinutes));
        emailSender.sendAuthCode(email, code);
    }

    public void verifyCode(String email, String mailCode) {
        String stored = passwordResetCodeStore.get(email);
        if (stored == null) throw new ServiceException(ErrorCode.EXPIRED_EMAIL_CODE);
        if (PasswordResetCodeStore.VERIFIED_STATE.equals(stored)) return;
        if (!mailCode.equals(stored)) throw new ServiceException(ErrorCode.INVALID_EMAIL_CODE);

        passwordResetCodeStore.markVerified(email, Duration.ofMinutes(expiryMinutes));
    }

    @Transactional
    public void resetPassword(String email, String newPassword) {
        String stored = passwordResetCodeStore.get(email);
        if (!"VERIFIED".equals(stored)) {
            throw new ServiceException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        User user = userRepositoryPort.findByUsername(email)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        user.changePassword(passwordEncoder.encode(newPassword));

        passwordResetCodeStore.delete(email);
    }
}