package com.chip.board.me.application.service.model;

public record MyRecordWeek(
        String week,
        int solvedCount,
        long score,
        Integer rank
) {}