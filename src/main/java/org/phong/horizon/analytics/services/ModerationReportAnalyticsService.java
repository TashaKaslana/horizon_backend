package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.report.enums.ModerationStatus;
import org.phong.horizon.report.infrastructure.persistence.repositories.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Arrays;

import static org.phong.horizon.analytics.utils.AnalyticsHelper.calculateTrend;

@Service
@AllArgsConstructor
public class ModerationReportAnalyticsService {
    private final ReportRepository reportRepository;

    /**
     * Get overview statistics for moderation reports dashboard
     */
    @Transactional
    public List<OverviewStatistic> getModerationOverviewStatistics() {
        ZoneId zone = ZoneOffset.UTC;

        // Calculate time periods
        Instant now = Instant.now();
        OffsetDateTime today = OffsetDateTime.ofInstant(now, zone);
        OffsetDateTime todayStart = today.toLocalDate().atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime yesterdayStart = todayStart.minusDays(1);
        OffsetDateTime weekAgoStart = todayStart.minusDays(7);
        OffsetDateTime previousWeekStart = todayStart.minusDays(14);
        OffsetDateTime previousWeekEnd = todayStart.minusDays(7);

        // 1. Pending Reports
        long pendingReports = reportRepository.countPendingReports();
        long thisWeekReports = reportRepository.countByCreatedAtBetween(weekAgoStart, todayStart);
        long lastWeekReports = reportRepository.countByCreatedAtBetween(previousWeekStart, previousWeekEnd);
        double pendingReportsTrend = calculateTrend(thisWeekReports, lastWeekReports);

        // 2. Resolved Today
        long resolvedToday = getResolvedCountForDay(todayStart);
        long resolvedYesterday = getResolvedCountForDay(yesterdayStart);
        double resolvedTrend = calculateTrend(resolvedToday, resolvedYesterday);

        // 3. Content Removed (Last 7 days)
        long contentRemoved = getContentRemovedCount(weekAgoStart, todayStart);
        long contentRemovedPreviousWeek = getContentRemovedCount(previousWeekStart, previousWeekEnd);
        double contentRemovedTrend = calculateTrend(contentRemoved, contentRemovedPreviousWeek);

        // 4. Users Actioned (Last 7 days)
        long usersActioned = getUsersActionedCount(weekAgoStart, todayStart);
        long usersActionedPreviousWeek = getUsersActionedCount(previousWeekStart, previousWeekEnd);
        double usersActionedTrend = calculateTrend(usersActioned, usersActionedPreviousWeek);

        return List.of(
            new OverviewStatistic(
                "Pending Reports",
                String.valueOf(pendingReports),
                pendingReportsTrend,
                pendingReportsTrend < 0 ? "Fewer reports than last week" : "More reports than last week",
                "Reports requiring admin review"
            ),
            new OverviewStatistic(
                "Resolved Today",
                String.valueOf(resolvedToday),
                resolvedTrend,
                resolvedTrend > 0 ? "More resolutions than yesterday" : "Fewer resolutions than yesterday",
                "Reports actioned and resolved today"
            ),
            new OverviewStatistic(
                "Content Removed (7d)",
                String.valueOf(contentRemoved),
                contentRemovedTrend,
                contentRemovedTrend > 0 ? "Increase in content takedowns" : "Decrease in content takedowns",
                "Posts/Comments removed in the last 7 days"
            ),
            new OverviewStatistic(
                "Users Actioned (7d)",
                String.valueOf(usersActioned),
                usersActionedTrend,
                usersActionedTrend < 0 ? "Fewer user actions this week" : "More user actions this week",
                "Users warned or banned in the last 7 days"
            )
        );
    }

    /**
     * Count reports resolved on a specific day
     */
    private long getResolvedCountForDay(OffsetDateTime dayStart) {
        OffsetDateTime dayEnd = dayStart.plusDays(1);

        return reportRepository.countByCreatedAtBetweenAndStatusIn(dayStart, dayEnd, Arrays.asList(
                ModerationStatus.RESOLVED,
                ModerationStatus.ACTIONTAKEN_CONTENTREMOVED,
                ModerationStatus.ACTIONTAKEN_USERBANNED,
                ModerationStatus.ACTIONTAKEN_USERWARNED
        ));
    }

    /**
     * Count content removed between dates
     */
    private long getContentRemovedCount(OffsetDateTime start, OffsetDateTime end) {
        return reportRepository.countByCreatedAtBetweenAndStatus(start, end, ModerationStatus.ACTIONTAKEN_CONTENTREMOVED);
    }

    /**
     * Count users warned or banned between dates
     */
    private long getUsersActionedCount(OffsetDateTime start, OffsetDateTime end) {
        return reportRepository.countByCreatedAtBetweenAndStatusIn(start, end, List.of(
                ModerationStatus.ACTIONTAKEN_USERBANNED, ModerationStatus.ACTIONTAKEN_USERWARNED
        ));
    }
}
