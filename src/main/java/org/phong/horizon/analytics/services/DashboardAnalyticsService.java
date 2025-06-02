package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.report.infrastructure.persistence.repositories.ReportRepository;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

import static org.phong.horizon.analytics.utils.AnalyticsHelper.calculateTrend;

/**
 * Centralized service for dashboard analytics across all domains.
 * Aggregates data from multiple repositories to provide a unified dashboard view.
 */
@Service
@AllArgsConstructor
public class DashboardAnalyticsService {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    // Inject the specific analytics services instead of directly using repositories
    private final UserAnalyticsService userAnalyticsService;
    private final PostAnalyticsService postAnalyticsService;
    private final CommentAnalyticsService commentAnalyticsService;

    /**
     * Retrieves the main dashboard overview statistics.
     * @return List of dashboard statistics for the main overview cards
     */
    @Transactional(readOnly = true)
    public List<OverviewStatistic> getDashboardOverview() {
        // Use UTC for consistent timezone handling
        ZoneId zone = ZoneOffset.UTC;

        // --- Users statistics ---
        // Total users
        long totalUsers = userRepository.count();
        // Monthly user trend
        OffsetDateTime thirtyDaysAgo = OffsetDateTime.now(zone).minusDays(30);
        OffsetDateTime sixtyDaysAgo = OffsetDateTime.now(zone).minusDays(60);
        Instant thirtyDaysAgoInstant = thirtyDaysAgo.toInstant();
        Instant sixtyDaysAgoInstant = sixtyDaysAgo.toInstant();
        Instant now = Instant.now();

        long usersLast30Days = userRepository.countByCreatedAtBetween(thirtyDaysAgoInstant, now);
        long usersPrevious30Days = userRepository.countByCreatedAtBetween(sixtyDaysAgoInstant, thirtyDaysAgoInstant);
        double userTrend = calculateTrend(usersLast30Days, usersPrevious30Days);

        // --- Posts statistics ---
        // Reuse the post analytics service for post statistics
        List<OverviewStatistic> postStats = postAnalyticsService.getPostAnalytics();
        OverviewStatistic postStat = postStats.stream()
                .filter(stat -> stat.getTitle().equals("Total Posts"))
                .findFirst()
                .orElse(new OverviewStatistic("Total Posts", "0", 0.0, "", ""));
        long totalPosts = Long.parseLong(postStat.getValue());
        double postTrend = postStat.getTrend();

        // --- Active users (users who have created content in the last 7 days)
        OffsetDateTime sevenDaysAgo = OffsetDateTime.now(zone).minusDays(7);
        OffsetDateTime fourteenDaysAgo = OffsetDateTime.now(zone).minusDays(14);
        long activeUsers = userRepository.countActiveUsersSince(sevenDaysAgo.toInstant());
        long previousActiveUsers = userRepository.countActiveUsersSince(fourteenDaysAgo.toInstant()) - activeUsers;
        double activeUsersTrend = calculateTrend(activeUsers, previousActiveUsers);

        // --- Reports pending review ---
        long pendingReports = reportRepository.countPendingReports();
        long previousWeekReports = reportRepository.countReportsCreatedBetween(
            fourteenDaysAgo,
            sevenDaysAgo
        );
        double reportsTrend = calculateTrend(pendingReports, previousWeekReports);
        // Invert the trend to make a reduction in reports a positive trend
        reportsTrend = -reportsTrend;

        return List.of(
            new OverviewStatistic(
                "Total Users",
                String.valueOf(totalUsers),
                userTrend,
                userTrend > 0 ? "Up " + formatTrend(userTrend) + "% this month" : "Down " + formatTrend(-userTrend) + "% this month",
                "User growth over the last 30 days"
            ),
            new OverviewStatistic(
                "Total Posts",
                String.valueOf(totalPosts),
                postTrend,
                "Steady content creation",
                "Posts uploaded in the last 30 days"
            ),
            new OverviewStatistic(
                "Active Users",
                String.valueOf(activeUsers),
                activeUsersTrend,
                activeUsersTrend > 8 ? "High engagement this week" : "Normal engagement levels",
                "Users active in the last 7 days"
            ),
            new OverviewStatistic(
                "Pending Reports",
                String.valueOf(pendingReports),
                reportsTrend,
                reportsTrend > 0 ? "Fewer reports than last week" : "More reports than last week",
                "Reports requiring admin review"
            )
        );
    }

    /**
     * Retrieves daily user registration counts for charting.
     * @param days Number of days to include
     * @return List of daily user counts
     */
    @Transactional(readOnly = true)
    public List<DailyCountDto> getUsersPerDay(int days) {
        // Reuse the UserAnalyticsService to get daily counts
        return userAnalyticsService.getFilledDailyUserCounts(days);
    }

    /**
     * Retrieves daily post creation counts for charting.
     * @param days Number of days to include
     * @return List of daily post counts
     */
    @Transactional(readOnly = true)
    public List<DailyCountDto> getPostsPerDay(int days) {
        // Reuse the PostAnalyticsService to get daily counts
        return postAnalyticsService.getFilledDailyPostCounts(days);
    }

    /**
     * Retrieves daily comment counts for charting.
     * @param days Number of days to include
     * @return List of daily comment counts
     */
    @Transactional(readOnly = true)
    public List<DailyCountDto> getCommentsPerDay(int days) {
        // Reuse the CommentAnalyticsService to get daily counts
        return commentAnalyticsService.getFilledDailyCommentCounts(days);
    }

    /**
     * Helper method to format trend percentages to one decimal place.
     */
    private String formatTrend(double trend) {
        return String.format("%.1f", trend);
    }
}
