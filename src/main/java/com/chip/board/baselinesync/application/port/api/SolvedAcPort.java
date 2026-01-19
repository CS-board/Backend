package com.chip.board.baselinesync.application.port.api;

import com.chip.board.baselinesync.infrastructure.api.dto.response.SolvedProblemPage;
import com.chip.board.baselinesync.infrastructure.persistence.dto.SolvedProblemItem;

import java.util.List;

public interface SolvedAcPort {
    boolean isCooldownActive();
    long nextAllowedAtMs();

    Integer fetchSolvedCountOrNull(String handle);
    List<SolvedProblemItem> fetchSolvedProblemPageOrNull(String handle, int page);

    SolvedProblemPage fetchSolvedProblemPageWithCountOrNull(String handle, int page);

}
