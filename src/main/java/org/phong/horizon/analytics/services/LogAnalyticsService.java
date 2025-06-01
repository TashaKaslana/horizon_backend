package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.admin.logentry.enums.LogSeverity;
import org.phong.horizon.admin.logentry.infrastructure.entities.LogEntry;
import org.phong.horizon.admin.logentry.infrastructure.repositories.LogEntryRepository;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.phong.horizon.analytics.utils.AnalyticsHelper.calculateTrend;

@Service
@AllArgsConstructor
public class LogAnalyticsService {

    private final LogEntryRepository logEntryRepository;

    /**
     * Get overview statistics for system logs
     */
    @Transactional
    public List<OverviewStatistic> getLogOverviewStatistics() {
        ZoneId zone = ZoneOffset.UTC;

        // Calculate time periods
        OffsetDateTime now = OffsetDateTime.now(zone);
        OffsetDateTime todayStart = now.truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime yesterdayStart = todayStart.minusDays(1);
        OffsetDateTime weekAgoStart = todayStart.minusDays(7);
        OffsetDateTime previousWeekStart = todayStart.minusDays(14);

        // 1. Error Logs Today
        long errorLogsToday = countBySeverityAndTimeRange(LogSeverity.ERROR, todayStart, now);
        long errorLogsYesterday = countBySeverityAndTimeRange(LogSeverity.ERROR, yesterdayStart, todayStart);
        double errorLogsTrend = calculateTrend(errorLogsToday, errorLogsYesterday);

        // 2. Critical Logs Today
        long criticalLogsToday = countBySeverityAndTimeRange(LogSeverity.CRITICAL, todayStart, now);
        long criticalLogsYesterday = countBySeverityAndTimeRange(LogSeverity.CRITICAL, yesterdayStart, todayStart);
        double criticalLogsTrend = calculateTrend(criticalLogsToday, criticalLogsYesterday);

        // 3. Error Logs (7d)
        long errorLogsWeek = countBySeverityAndTimeRange(LogSeverity.ERROR, weekAgoStart, now);
        long errorLogsPrevWeek = countBySeverityAndTimeRange(LogSeverity.ERROR, previousWeekStart, weekAgoStart);
        double errorLogsWeekTrend = calculateTrend(errorLogsWeek, errorLogsPrevWeek);

        // 4. Critical Logs (7d)
        long criticalLogsWeek = countBySeverityAndTimeRange(LogSeverity.CRITICAL, weekAgoStart, now);
        long criticalLogsPrevWeek = countBySeverityAndTimeRange(LogSeverity.CRITICAL, previousWeekStart, weekAgoStart);
        double criticalLogsWeekTrend = calculateTrend(criticalLogsWeek, criticalLogsPrevWeek);

        return List.of(
            new OverviewStatistic(
                "Error Logs Today",
                String.valueOf(errorLogsToday),
                errorLogsTrend,
                errorLogsTrend > 0 ? "More errors than yesterday" : "Fewer errors than yesterday",
                "Error-level logs recorded today"
            ),
            new OverviewStatistic(
                "Critical Logs Today",
                String.valueOf(criticalLogsToday),
                criticalLogsTrend,
                criticalLogsTrend > 0 ? "More critical issues than yesterday" : "Fewer critical issues than yesterday",
                "Critical-level logs recorded today"
            ),
            new OverviewStatistic(
                "Error Logs (7d)",
                String.valueOf(errorLogsWeek),
                errorLogsWeekTrend,
                errorLogsWeekTrend > 0 ? "Increase from previous week" : "Decrease from previous week",
                "Total error logs in the last 7 days"
            ),
            new OverviewStatistic(
                "Critical Logs (7d)",
                String.valueOf(criticalLogsWeek),
                criticalLogsWeekTrend,
                criticalLogsWeekTrend > 0 ? "Increase from previous week" : "Decrease from previous week",
                "Total critical logs in the last 7 days"
            )
        );
    }

    /**
     * Count logs by severity and time range
     */
    private long countBySeverityAndTimeRange(LogSeverity severity, OffsetDateTime from, OffsetDateTime to) {
        Specification<LogEntry> spec = Specification.where(null);

        if (severity != null) {
            spec = spec.and((root, _, cb) -> cb.equal(root.get("severity"), severity));
        }

        if (from != null) {
            spec = spec.and((root, _, cb) -> cb.greaterThanOrEqualTo(root.get("timestamp"), from));
        }

        if (to != null) {
            spec = spec.and((root, _, cb) -> cb.lessThan(root.get("timestamp"), to));
        }

        return logEntryRepository.count(spec);
    }

    /**
     * Get daily error and critical log counts for the specified number of days
     */
    @Transactional
    public List<DailyCountDto> getDailyErrorAndCriticalLogCounts(int days) {
        ZoneId zone = ZoneOffset.UTC;
        OffsetDateTime endDate = OffsetDateTime.now(zone);
        OffsetDateTime startDate = endDate.minusDays(days);

        // Directly use JPA query method instead of Specifications
        List<Object[]> dailyCounts = logEntryRepository.findDailyErrorAndCriticalCounts(startDate, endDate);

        // Convert to map for easy lookup
        Map<LocalDate, Long> countByDay = new HashMap<>();
        for (Object[] row : dailyCounts) {
            LocalDate date = (LocalDate) row[0];
            Long count = ((Number) row[1]).longValue();
            countByDay.put(date, count);
        }

        // Fill in missing days with zeros
        List<DailyCountDto> result = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = endDate.minusDays(i).toLocalDate();
            Long count = countByDay.getOrDefault(date, 0L);
            result.add(new DailyCountDto(date, count));
        }

        // Sort by date (ascending order)
        return result.stream()
            .sorted(Comparator.comparing(DailyCountDto::getDate))
            .collect(Collectors.toList());
    }

    /**
     * Get daily breakdown of error logs by severity
     */
    @Transactional
    public Map<String, List<DailyCountDto>> getDailyLogsBySeverity(int days) {
        ZoneId zone = ZoneOffset.UTC;
        OffsetDateTime endDate = OffsetDateTime.now(zone);
        OffsetDateTime startDate = endDate.minusDays(days);

        // Get all error and critical logs in date range
        Specification<LogEntry> spec = (root, _, cb) ->
            cb.and(
                cb.between(root.get("timestamp"), startDate, endDate),
                cb.or(
                    cb.equal(root.get("severity"), LogSeverity.ERROR),
                    cb.equal(root.get("severity"), LogSeverity.CRITICAL)
                )
            );

        List<LogEntry> logs = logEntryRepository.findAll(spec);

        // Group by severity and day
        Map<LogSeverity, Map<LocalDate, Long>> groupedBySeverityAndDay = logs.stream()
            .collect(Collectors.groupingBy(
                LogEntry::getSeverity,
                Collectors.groupingBy(
                    log -> log.getTimestamp().toLocalDate(),
                    Collectors.counting()
                )
            ));

        // Convert to format needed for frontend charts
        // Fill in all days including zeros
        // Sort by date

        return groupedBySeverityAndDay.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().name(),
                entry -> {
                    List<DailyCountDto> dailyCounts = new ArrayList<>();
                    Map<LocalDate, Long> dayCounts = entry.getValue();

                    // Fill in all days including zeros
                    for (int i = 0; i < days; i++) {
                        LocalDate date = endDate.minusDays(i).toLocalDate();
                        Long count = dayCounts.getOrDefault(date, 0L);
                        dailyCounts.add(new DailyCountDto(date, count));
                    }

                    // Sort by date
                    return dailyCounts.stream()
                        .sorted(Comparator.comparing(DailyCountDto::getDate))
                        .collect(Collectors.toList());
                }
            ));
    }
}
