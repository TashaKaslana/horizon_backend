package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostInteractionRepository;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostRepository;
import org.phong.horizon.post.infrastructure.persistence.repositories.ViewRepository;
import org.phong.horizon.report.enums.ModerationItemType;
import org.phong.horizon.report.infrastructure.persistence.repositories.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostAnalyticsService {
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final ViewRepository viewRepository;
    private final PostInteractionRepository postInteractionRepository;

    @Transactional
    public List<OverviewStatistic> getPostAnalytics() {
        long totalPosts = postRepository.count();

        // Use system default zone for logical days (or use UTC)
        ZoneId zone = ZoneOffset.UTC;

        // 1. Today
        Instant todayStart = LocalDate.now(zone).atStartOfDay(zone).toInstant();
        long postsToday = postRepository.countByCreatedAtAfter(todayStart);

        // 2. Yesterday
        Instant yesterdayStart = LocalDate.now(zone).minusDays(1).atStartOfDay(zone).toInstant();
        long postsYesterday = postRepository.countByCreatedAtBetween(yesterdayStart, todayStart);

        double postTrend = calculateTrend(postsToday, postsYesterday);

        // 3. Reported posts
        long reportedPosts = reportRepository.countByItemType(ModerationItemType.POST);

        // 4. Average engagement in last 7 days
        LocalDateTime weekAgo = LocalDateTime.now().minus(Duration.ofDays(7));
        Instant weekAgoInstant = weekAgo.atZone(zone).toInstant();
        double views =  viewRepository.countByViewedAtAfter(weekAgo);
        long interactions = postInteractionRepository.countByCreatedAtAfter(weekAgoInstant);
        double avgEngagement = interactions == 0 ? 0.0 : views / interactions;

        return List.of(
                new OverviewStatistic(
                        "Total Posts",
                        String.valueOf(totalPosts),
                        4.1,
                        "Steady content growth",
                        "Total posts uploaded to date"
                ),
                new OverviewStatistic(
                        "Posts Today",
                        String.valueOf(postsToday),
                        postTrend,
                        postTrend >= 0 ? "Slight increase from yesterday" : "Drop compared to yesterday",
                        "Posts created in the last 24 hours"
                ),
                new OverviewStatistic(
                        "Reported Posts",
                        String.valueOf(reportedPosts),
                        -8.4,
                        "Fewer reports than last week",
                        "Posts currently flagged by users"
                ),
                new OverviewStatistic(
                        "Avg. Engagement",
                        String.format("%.1f", avgEngagement),
                        5.9,
                        "Up from last week",
                        "Avg likes/comments per post (7d)"
                )
        );
    }

    @Transactional
    public List<OverviewStatistic.DailyPostCountDto> getFilledDailyPostCounts(int days) {
        Instant from = Instant.now().minus(Duration.ofDays(days));
        List<Object[]> raw = postRepository.countPostsPerDay(from);

        Map<LocalDate, Long> countMap = raw.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate(),
                        row -> ((Number) row[1]).longValue()
                ));

        List<OverviewStatistic.DailyPostCountDto> result = new ArrayList<>();
        LocalDate start = LocalDate.now().minusDays(days);
        LocalDate end = LocalDate.now();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            long count = countMap.getOrDefault(date, 0L);
            result.add(new OverviewStatistic.DailyPostCountDto(date, count));
        }

        return result;
    }

    private double calculateTrend(long current, long previous) {
        if (previous == 0) return current == 0 ? 0.0 : 100.0;
        return ((double) (current - previous) / previous) * 100;
    }
}
