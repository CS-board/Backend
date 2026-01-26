package com.chip.board.me.application.service.model;

import java.util.List;

public record PagedResult<T>(
        List<T> items,
        int page,
        int size,
        boolean hasNext
) {}