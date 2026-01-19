package com.chip.board.baselinesync.infrastructure.api.dto.response;

import com.chip.board.baselinesync.infrastructure.persistence.dto.SolvedProblemItem;

import java.util.List;

public record SolvedProblemPage(
        int totalSolvedCount,
        List<SolvedProblemItem> items
) {}