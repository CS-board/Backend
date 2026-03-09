package com.chip.board.solvedac.application.service;

public final class TierMapper {
    private TierMapper() {}

    public static String toTierCode(int level) {
        if (level <= 0) return "0";
        if (level <= 5)  return "b" + (6 - level);      // 1->b5 ... 5->b1
        if (level <= 10) return "s" + (11 - level);     // 6->s5 ... 10->s1
        if (level <= 15) return "g" + (16 - level);     // 11->g5 ... 15->g1
        if (level <= 20) return "p" + (21 - level);     // 16->p5 ... 20->p1
        if (level <= 25) return "d" + (26 - level);     // 21->d5 ... 25->d1
        if (level <= 30) return "r" + (31 - level);     // 26->r5 ... 30->r1
        return Integer.toString(level);
    }
}
