package com.chip.board.solvedac.application.service;

import com.chip.board.solvedac.presentation.dto.request.SolvedAcRandomProblemsRequest;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public final class SolvedAcSearchQueryBuilder {

    private SolvedAcSearchQueryBuilder() {}

    public static String build(
            List<String> algorithmTags,
            Integer minSolvedUserCount,
            Integer maxSolvedUserCount,
            String tierFrom,
            String tierTo,
            SolvedAcRandomProblemsRequest.SolvedFilter solvedFilter,
            String handleOrNull
    ) {
        StringJoiner query = new StringJoiner(" ");

        // ✅ 한국어 문제만 강제: %ko :contentReference[oaicite:16]{index=16}
        query.add("%ko");

        // ✅ 티어 범위: *g5..g3 :contentReference[oaicite:17]{index=17}
        query.add("*" + tierFrom + ".." + tierTo);

        // ✅ 푼 사람 수(solved user count): s#100..500 :contentReference[oaicite:18]{index=18}
        String solvedRange = toRange(minSolvedUserCount, maxSolvedUserCount);
        if (solvedRange != null) {
            query.add("s#" + solvedRange);
        }

        // ✅ 알고리즘 태그 OR: (#bfs | #dfs) :contentReference[oaicite:19]{index=19}
        if (algorithmTags != null && !algorithmTags.isEmpty()) {
            if (algorithmTags.size() == 1) {
                query.add("#" + algorithmTags.get(0));
            } else {
                String orExpr = algorithmTags.stream()
                        .map(t -> "#" + t)
                        .collect(Collectors.joining(" | "));
                query.add("( " + orExpr + " )");
            }
        }

        // ✅ 푼/안푼: @handle 또는 -@handle :contentReference[oaicite:20]{index=20}
        if (solvedFilter != null && solvedFilter != SolvedAcRandomProblemsRequest.SolvedFilter.ANY) {
            if (handleOrNull == null || handleOrNull.isBlank()) {
                throw new IllegalArgumentException("solvedFilter가 ANY가 아니면 handle이 필요합니다.");
            }
            switch (solvedFilter) {
                case SOLVED -> query.add("@" + handleOrNull);
                case UNSOLVED -> query.add("-@" + handleOrNull);
                default -> {}
            }
        }

        return query.toString();
    }

    private static String toRange(Integer min, Integer max) {
        if (min == null && max == null) return null;
        if (min != null && max != null) return min + ".." + max;
        if (min != null) return min + "..";
        return ".." + max;
    }
}