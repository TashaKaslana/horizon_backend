package org.phong.horizon.analytics.dtos;

import java.time.LocalDate;

public record DailyPendingAndResolvedDto(LocalDate date, long pendingCount, long resolvedCount) {
}
