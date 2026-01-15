package com.chip.board.register.application.port;

public interface EmailSender {
    void sendAuthCode(String email, int code);
    void sendTempPassword(String email, String tempPassword);
}