package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyPendingAndResolvedDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.report.enums.ModerationItemType;
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
     * Get overview statistics for all moderation reports
     */
    @Transactional
    public List<OverviewStatistic> getModerationOverviewStatistics() {
        return getModerationOverviewStatistics(null);
    }

    /**
     * Get overview statistics for moderation reports filtered by item type
     * @param itemType The type of item to filter by (USER, POST, COMMENT), or null for all items
     */
    @Transactional
    public List<OverviewStatistic> getModerationOverviewStatistics(ModerationItemType itemType) {
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
        long pendingReports = (itemType == null) ?
                reportRepository.countPendingReports() :
                reportRepository.countPendingReportsByItemType(itemType);

        long thisWeekReports = (itemType == null) ?
                reportRepository.countByCreatedAtBetween(weekAgoStart, todayStart) :
                reportRepository.countByCreatedAtBetweenAndItemType(weekAgoStart, todayStart, itemType);

        long lastWeekReports = (itemType == null) ?
                reportRepository.countByCreatedAtBetween(previousWeekStart, previousWeekEnd) :
                reportRepository.countByCreatedAtBetweenAndItemType(previousWeekStart, previousWeekEnd, itemType);

        double pendingReportsTrend = calculateTrend(thisWeekReports, lastWeekReports);

        // 2. Resolved Today
        long resolvedToday = getResolvedCount(todayStart, todayStart.plusDays(1), itemType);
        long resolvedYesterday = getResolvedCount(yesterdayStart, yesterdayStart.plusDays(1), itemType);
        double resolvedTrend = calculateTrend(resolvedToday, resolvedYesterday);

        // 3. Content Removed (Last 7 days)
        long contentRemoved = getContentRemovedCount(weekAgoStart, todayStart, itemType);
        long contentRemovedPreviousWeek = getContentRemovedCount(previousWeekStart, previousWeekEnd, itemType);
        double contentRemovedTrend = calculateTrend(contentRemoved, contentRemovedPreviousWeek);

        // 4. Users Actioned (Last 7 days)
        long usersActioned = getUsersActionedCount(weekAgoStart, todayStart, itemType);
        long usersActionedPreviousWeek = getUsersActionedCount(previousWeekStart, previousWeekEnd, itemType);
        double usersActionedTrend = calculateTrend(usersActioned, usersActionedPreviousWeek);

        // Generate title prefix based on item type, if applicable
        String titlePrefix = "";
        if (itemType != null) {
            titlePrefix = itemType.name().charAt(0) + itemType.name().substring(1).toLowerCase() + " ";
        }

        return List.of(
                new OverviewStatistic(
                        titlePrefix + "Pending Reports",
                        String.valueOf(pendingReports),
                        pendingReportsTrend,
                        pendingReportsTrend < 0 ? "Fewer reports than last week" : "More reports than last week",
                        titlePrefix + "Reports requiring admin review"
                ),
                new OverviewStatistic(
                        titlePrefix + "Resolved Today",
                        String.valueOf(resolvedToday),
                        resolvedTrend,
                        resolvedTrend > 0 ? "More resolutions than yesterday" : "Fewer resolutions than yesterday",
                        titlePrefix + "Reports actioned and resolved today"
                ),
                new OverviewStatistic(
                        titlePrefix + "Content Removed (7d)",
                        String.valueOf(contentRemoved),
                        contentRemovedTrend,
                        contentRemovedTrend > 0 ? "Increase in content takedowns" : "Decrease in content takedowns",
                        titlePrefix + "Posts/Comments removed in the last 7 days"
                ),
                new OverviewStatistic(
                        titlePrefix + "Users Actioned (7d)",
                        String.valueOf(usersActioned),
                        usersActionedTrend,
                        usersActionedTrend < 0 ? "Fewer user actions this week" : "More user actions this week",
                        titlePrefix + "Users warned or banned in the last 7 days"
                )
        );
    }

    /**
     * Get daily pending and resolved counts
     * @param days Number of days to get data for
     * @return List of daily counts
     */
    public List<DailyPendingAndResolvedDto> getDailyPendingAndResolvedCounts(int days) {
        return getDailyPendingAndResolvedCounts(days, null);
    }

    /**
     * Get daily pending and resolved counts filtered by item type
     * @param days Number of days to get data for
     * @param itemType The type of item to filter by (USER, POST, COMMENT), or null for all items
     * @return List of daily counts
     */
    public List<DailyPendingAndResolvedDto> getDailyPendingAndResolvedCounts(int days, ModerationItemType itemType) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime startDate = now.minusDays(days);

        // Get counts from repository based on itemType filter
        List<Object[]> pendingCount;
        List<Object[]> resolvedCount;

        if (itemType == null) {
            // Fix parameter order: startDate should be first parameter, now should be second
            pendingCount = reportRepository.countDailyPendingReports(startDate, now);
            resolvedCount = reportRepository.countDailyResolvedReports(startDate, now);
        } else {
            // Fix parameter order: startDate should be first parameter, now should be second
            pendingCount = reportRepository.countDailyPendingReportsByItemType(startDate, now, itemType);
            resolvedCount = reportRepository.countDailyResolvedReportsByItemType(startDate, now, itemType);
        }

        // Create a list filled with zero-value entries for all days
        List<DailyPendingAndResolvedDto> dailies = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            dailies.add(new DailyPendingAndResolvedDto(now.minusDays(i).toLocalDate(), 0, 0));
        }

        // Update the list with actual values from the database queries
        for (Object[] entry : pendingCount) {
            for (DailyPendingAndResolvedDto dto : dailies) {
                if (dto.getDate().equals(((java.sql.Date) entry[0]).toLocalDate())) {
                    dto.setPendingCount(((Number) entry[1]).longValue());
                    break;
                }
            }
        }

        for (Object[] entry : resolvedCount) {
            for (DailyPendingAndResolvedDto dto : dailies) {
                if (dto.getDate().equals(((java.sql.Date) entry[0]).toLocalDate())) {
                    dto.setResolvedCount(((Number) entry[1]).longValue());
                    break;
                }
            }
        }

        return dailies;
    }

    /**
     * Count reports resolved on a specific day with optional item type filter
     */
    private long getResolvedCount(OffsetDateTime dayStart, OffsetDateTime dayEnd, ModerationItemType itemType) {
        if (itemType == null) {
            return reportRepository.countByCreatedAtBetweenAndStatusIn(dayStart, dayEnd, List.of(
                    ModerationStatus.RESOLVED,
                    ModerationStatus.ACTIONTAKEN_CONTENTREMOVED,
                    ModerationStatus.ACTIONTAKEN_USERBANNED,
                    ModerationStatus.ACTIONTAKEN_USERWARNED
            ));
        } else {
            return reportRepository.countByCreatedAtBetweenAndStatusInAndItemType(dayStart, dayEnd, List.of(
                    ModerationStatus.RESOLVED,
                    ModerationStatus.ACTIONTAKEN_CONTENTREMOVED,
                    ModerationStatus.ACTIONTAKEN_USERBANNED,
                    ModerationStatus.ACTIONTAKEN_USERWARNED
            ), itemType);
        }
    }

    /**
     * Count content removed between dates with optional item type filter
     */
    private long getContentRemovedCount(OffsetDateTime start, OffsetDateTime end, ModerationItemType itemType) {
        if (itemType == null) {
            return reportRepository.countByCreatedAtBetweenAndStatus(start, end, ModerationStatus.ACTIONTAKEN_CONTENTREMOVED);
        } else {
            return reportRepository.countByCreatedAtBetweenAndStatusAndItemType(start, end,
                    ModerationStatus.ACTIONTAKEN_CONTENTREMOVED, itemType);
        }
    }

    /**
     * Count users warned or banned between dates with optional item type filter
     */
    private long getUsersActionedCount(OffsetDateTime start, OffsetDateTime end, ModerationItemType itemType) {
        if (itemType == null) {
            return reportRepository.countByCreatedAtBetweenAndStatusIn(start, end, List.of(
                    ModerationStatus.ACTIONTAKEN_USERBANNED, ModerationStatus.ACTIONTAKEN_USERWARNED
            ));
        } else {
            return reportRepository.countByCreatedAtBetweenAndStatusInAndItemType(start, end, List.of(
                    ModerationStatus.ACTIONTAKEN_USERBANNED, ModerationStatus.ACTIONTAKEN_USERWARNED
            ), itemType);
        }
    }
}
