package com.chip.board.register.application.command;

public record VerifyEmailCommand(String email, int code) {}
