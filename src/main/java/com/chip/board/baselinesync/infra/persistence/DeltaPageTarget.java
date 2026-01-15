package com.chip.board.baselinesync.infra.persistence;

public record DeltaPageTarget(long userId, String bojHandle, int nextPage, int observedSolvedCount) {}
