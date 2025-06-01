package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.dtos.TimeSeriesDataPoint;
import org.phong.horizon.comment.infrastructure.persistence.repositories.CommentRepository;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostRepository;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.phong.horizon.report.infrastructure.persistence.repositories.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized service for dashboard analytics across all domains.
 * Aggregates data from multiple repositories to provide a unified dashboard view.
 */
@Service
@AllArgsConstructor
public class DashboardAnalyticsService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;

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
        long totalPosts = postRepository.count();
        long postsLast30Days = postRepository.countByCreatedAtAfter(thirtyDaysAgoInstant);
        long postsPrevious30Days = postRepository.countByCreatedAtBetween(sixtyDaysAgoInstant, thirtyDaysAgoInstant);
        double postTrend = calculateTrend(postsLast30Days, postsPrevious30Days);

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
     * @return List of time series data points
     */
    @Transactional(readOnly = true)
    public List<TimeSeriesDataPoint> getUsersPerDay(int days) {
        Instant from = Instant.now().minus(Duration.ofDays(days));
        List<Object[]> raw = userRepository.countUsersPerDay(from);

        return convertToTimeSeriesData(raw, days);
    }

    /**
     * Retrieves daily post creation counts for charting.
     * @param days Number of days to include
     * @return List of time series data points
     */
    @Transactional(readOnly = true)
    public List<TimeSeriesDataPoint> getPostsPerDay(int days) {
        Instant from = Instant.now().minus(Duration.ofDays(days));
        List<Object[]> raw = postRepository.countPostsPerDay(from);

        return convertToTimeSeriesData(raw, days);
    }

    /**
     * Retrieves daily comment counts for charting.
     * @param days Number of days to include
     * @return List of time series data points
     */
    @Transactional(readOnly = true)
    public List<TimeSeriesDataPoint> getCommentsPerDay(int days) {
        Instant from = Instant.now().minus(Duration.ofDays(days));
        List<Object[]> raw = commentRepository.countCommentsPerDay(from);

        return convertToTimeSeriesData(raw, days);
    }

    /**
     * Helper method to calculate percentage trend between current and previous values.
     */
    private double calculateTrend(long current, long previous) {
        if (previous == 0) return current == 0 ? 0.0 : 100.0;
        return ((double) (current - previous) / previous) * 100;
    }

    /**
     * Helper method to format trend percentages to one decimal place.
     */
    private String formatTrend(double trend) {
        return String.format("%.1f", trend);
    }

    /**
     * Helper method to convert database results to TimeSeriesDataPoint objects
     * and fill in missing dates with zero values.
     */
    private List<TimeSeriesDataPoint> convertToTimeSeriesData(List<Object[]> rawData, int days) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

        // Convert raw data to a map of date -> count
        Map<LocalDate, Long> countMap = rawData.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate(),
                        row -> ((Number) row[1]).longValue()
                ));

        // Create result list with all days filled in
        List<TimeSeriesDataPoint> result = new ArrayList<>();
        LocalDate start = LocalDate.now().minusDays(days - 1);
        LocalDate end = LocalDate.now();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String formattedDate = date.format(formatter);
            long count = countMap.getOrDefault(date, 0L);
            result.add(new TimeSeriesDataPoint(formattedDate, count));
        }

        return result;
    }
}
