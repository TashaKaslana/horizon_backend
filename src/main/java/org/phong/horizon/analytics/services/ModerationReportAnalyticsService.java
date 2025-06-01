package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyPendingAndResolvedDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.report.enums.ModerationStatus;
import org.phong.horizon.report.infrastructure.persistence.repositories.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

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
        long resolvedToday = getResolvedCount(todayStart, todayStart.plusDays(1));
        long resolvedYesterday = getResolvedCount(yesterdayStart, yesterdayStart.plusDays(1));
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

    public List<DailyPendingAndResolvedDto> getDailyPendingAndResolvedCounts(int days) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<Object[]> pendingCount = reportRepository.countDailyPendingReports(now, now.minusDays(days));
        List<Object[]> resolvedCount = reportRepository.countDailyResolvedReports(now, now.minusDays(days));

        List<DailyPendingAndResolvedDto> dailies = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            dailies.add(new DailyPendingAndResolvedDto(now.minusDays(i).toLocalDate(), 0, 0));
        }

        for (int i = 0; i < days; i++) {
            long pendingCountAtIndex = pendingCount.size() > i ? (long) pendingCount.get(i)[1] : 0;
            long resolvedCountAtIndex = resolvedCount.size() > i ? (long) resolvedCount.get(i)[1] : 0;
            dailies.add(
                    new DailyPendingAndResolvedDto(now.minusDays(i).toLocalDate(),
                            pendingCountAtIndex,
                            resolvedCountAtIndex
                    ));
        }

        return dailies;
    }

    /**
     * Count reports resolved on a specific day
     */
    private long getResolvedCount(OffsetDateTime dayStart, OffsetDateTime dayEnd) {
        return reportRepository.countByCreatedAtBetweenAndStatusIn(dayStart, dayEnd, List.of(
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
