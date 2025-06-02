package org.phong.horizon.analytics.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Setter
@Getter
public final class DailyPendingAndResolvedDto {
    private LocalDate date;
    private long pendingCount;
    private long resolvedCount;
}
