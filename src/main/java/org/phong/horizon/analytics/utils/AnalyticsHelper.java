package org.phong.horizon.analytics.utils;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AnalyticsHelper {
    public static List<DailyCountDto> getDailyCountDtos(int days, List<Object[]> raw) {
        Map<LocalDate, Long> countMap = raw.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate(),
                        row -> ((Number) row[1]).longValue()
                ));

        List<DailyCountDto> result = new ArrayList<>();
        LocalDate start = LocalDate.now().minusDays(days);
        LocalDate end = LocalDate.now();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            long count = countMap.getOrDefault(date, 0L);
            result.add(new DailyCountDto(date, count));
        }

        return result;
    }

    public static double calculateTrend(long current, long previous) {
        if (previous == 0) return current == 0 ? 0.0 : 100.0;
        return ((double) (current - previous) / previous) * 100;
    }
}
