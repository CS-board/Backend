package com.chip.board.baselinesync.infra.persistence;

public record BaselineTarget(
        long userId,
        String bojHandle,
        int nextPage
) {}
