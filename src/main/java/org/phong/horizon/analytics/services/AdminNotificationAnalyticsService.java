package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.admin.notification.enums.AdminNotificationType;
import org.phong.horizon.admin.notification.enums.NotificationSeverity;
import org.phong.horizon.admin.notification.infrastructure.entities.AdminNotification;
import org.phong.horizon.admin.notification.infrastructure.repositories.AdminNotificationRepository;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.core.services.LocalizationProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.phong.horizon.analytics.utils.AnalyticsHelper.calculateTrend;

@Service
@AllArgsConstructor
public class AdminNotificationAnalyticsService {

    private final AdminNotificationRepository adminNotificationRepository;

    /**
     * Get overview statistics for admin notifications
     */
    @Transactional
    @Cacheable("admin-notification-overview")
    public List<OverviewStatistic> getAdminNotificationOverview() {
        ZoneId zone = ZoneOffset.UTC;

        // Calculate time periods
        OffsetDateTime now = OffsetDateTime.now(zone);
        OffsetDateTime todayStart = now.truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime yesterdayStart = todayStart.minusDays(1);
        OffsetDateTime weekAgoStart = todayStart.minusDays(7);
        OffsetDateTime previousWeekStart = todayStart.minusDays(14);

        // 1. Unread Notifications
        long unreadNotifications = adminNotificationRepository.countUnread();
        // Get trend by comparing to yesterday
        long unreadYesterday = adminNotificationRepository.countUnreadAtDate(yesterdayStart);
        double unreadTrend = calculateTrend(unreadNotifications, unreadYesterday);

        // 2. Critical Alerts
        long criticalAlerts = adminNotificationRepository.countBySeverityAndTimeRange(NotificationSeverity.CRITICAL, null, now);
        long criticalAlertsYesterday = adminNotificationRepository.countBySeverityAndTimeRange(NotificationSeverity.CRITICAL, null, yesterdayStart);
        double criticalTrend = calculateTrend(criticalAlerts, criticalAlertsYesterday);

        // 3. System Issues (7d)
        long systemIssues = adminNotificationRepository.countByTypeAndTimeRange(AdminNotificationType.ERROR, weekAgoStart, now);
        long systemIssuesPrevWeek = adminNotificationRepository.countByTypeAndTimeRange(AdminNotificationType.ERROR, previousWeekStart, weekAgoStart);
        double systemIssuesTrend = calculateTrend(systemIssues, systemIssuesPrevWeek);

        // 4. New Reports (24h)
        long newReports = adminNotificationRepository.countByTypeAndTimeRange(AdminNotificationType.REPORT, yesterdayStart, now);
        long newReportsPrevDay = adminNotificationRepository.countByTypeAndTimeRange(AdminNotificationType.REPORT, yesterdayStart.minusDays(1), yesterdayStart);
        double newReportsTrend = calculateTrend(newReports, newReportsPrevDay);

        return List.of(
            new OverviewStatistic(
                LocalizationProvider.getMessage("analytics.admin_notifications.unread.title"),
                String.valueOf(unreadNotifications),
                unreadTrend,
                unreadTrend > 0
                        ? LocalizationProvider.getMessage("analytics.admin_notifications.unread.message.more")
                        : LocalizationProvider.getMessage("analytics.admin_notifications.unread.message.fewer"),
                LocalizationProvider.getMessage("analytics.admin_notifications.unread.description")
            ),
            new OverviewStatistic(
                LocalizationProvider.getMessage("analytics.admin_notifications.critical.title"),
                String.valueOf(criticalAlerts),
                criticalTrend,
                criticalTrend > 0
                        ? LocalizationProvider.getMessage("analytics.admin_notifications.critical.message.more")
                        : LocalizationProvider.getMessage("analytics.admin_notifications.critical.message.fewer"),
                LocalizationProvider.getMessage("analytics.admin_notifications.critical.description")
            ),
            new OverviewStatistic(
                LocalizationProvider.getMessage("analytics.admin_notifications.system_issues.title"),
                String.valueOf(systemIssues),
                systemIssuesTrend,
                systemIssuesTrend > 0
                        ? LocalizationProvider.getMessage("analytics.admin_notifications.system_issues.message.more")
                        : LocalizationProvider.getMessage("analytics.admin_notifications.system_issues.message.fewer"),
                LocalizationProvider.getMessage("analytics.admin_notifications.system_issues.description")
            ),
            new OverviewStatistic(
                LocalizationProvider.getMessage("analytics.admin_notifications.new_reports.title"),
                String.valueOf(newReports),
                newReportsTrend,
                newReportsTrend > 0
                        ? LocalizationProvider.getMessage("analytics.admin_notifications.new_reports.message.increase")
                        : LocalizationProvider.getMessage("analytics.admin_notifications.new_reports.message.decrease"),
                LocalizationProvider.getMessage("analytics.admin_notifications.new_reports.description")
            )
        );
    }

    /**
     * Get notification counts per day for the last X days
     */
    @Transactional
    public List<DailyCountDto> getNotificationTrendData(int days) {
        ZoneId zone = ZoneOffset.UTC;
        OffsetDateTime endDate = OffsetDateTime.now(zone);
        OffsetDateTime startDate = endDate.minusDays(days);

        // Get notification counts per day using the repository method
        List<Object[]> rawData = adminNotificationRepository.countNotificationsPerDay(startDate);

        // Convert to map for easy lookup
        Map<LocalDate, Long> countByDay = rawData.stream()
            .collect(Collectors.toMap(
                row -> (LocalDate) row[0],
                row -> (Long) row[1]
            ));

        // Fill in missing days
        List<DailyCountDto> result = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = endDate.minusDays(i).toLocalDate();
            Long count = countByDay.getOrDefault(date, 0L);
            result.add(new DailyCountDto(date, count));
        }

        // Sort by date (chronological order)
        return result.stream()
            .sorted(Comparator.comparing(DailyCountDto::getDate))
            .collect(Collectors.toList());
    }

    /**
     * Get notification counts by severity
     */
    @Transactional
    public Map<String, Long> getNotificationsBySeverity() {
        List<AdminNotification> notifications = adminNotificationRepository.findAll();

        return notifications.stream()
            .collect(Collectors.groupingBy(
                n -> n.getSeverity().name(),
                Collectors.counting()
            ));
    }

    /**
     * Get notification counts by type
     */
    @Transactional
    public Map<String, Long> getNotificationsByType() {
        List<AdminNotification> notifications = adminNotificationRepository.findAll();

        return notifications.stream()
            .collect(Collectors.groupingBy(
                n -> n.getType().name(),
                Collectors.counting()
            ));
    }
}
