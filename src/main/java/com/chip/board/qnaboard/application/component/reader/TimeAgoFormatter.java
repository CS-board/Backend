package com.chip.board.qnaboard.application.component.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
@RequiredArgsConstructor
public class TimeAgoFormatter {

    private final Clock clock;

    public String format(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now(clock); // clock이 Asia/Seoul
        Duration d = Duration.between(createdAt, now);

        long minutes = d.toMinutes();
        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";

        long hours = d.toHours();
        if (hours < 24) return hours + "시간 전";

        long days = d.toDays();
        if (days < 30) return days + "일 전";

        long months = days / 30;
        if (months < 12) return months + "개월 전";

        long years = months / 12;
        return years + "년 전";
    }
}