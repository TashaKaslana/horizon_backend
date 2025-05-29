package org.phong.horizon.post.dtos;

import java.time.LocalDate;

public record DailyPostCountDto(
        LocalDate date,
        long count
) {}

