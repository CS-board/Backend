package com.chip.board.register.application.command;

public record RegisterUserCommand(
        String username,
        String password,
        String name,
        String department,
        String studentId,
        int grade,
        String bojId,
        String phoneNumber
) {}