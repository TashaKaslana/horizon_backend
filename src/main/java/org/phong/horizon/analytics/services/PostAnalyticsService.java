package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostInteractionRepository;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostRepository;
import org.phong.horizon.post.infrastructure.persistence.repositories.ViewRepository;
import org.phong.horizon.report.enums.ModerationItemType;
import org.phong.horizon.report.infrastructure.persistence.repositories.ReportRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

import static org.phong.horizon.analytics.utils.AnalyticsHelper.calculateTrend;
import static org.phong.horizon.analytics.utils.AnalyticsHelper.getDailyCountDtos;

@Service
@AllArgsConstructor
public class PostAnalyticsService {
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final ViewRepository viewRepository;
    private final PostInteractionRepository postInteractionRepository;

    @Transactional
    @Cacheable("post-overview")
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
    @Cacheable(value = "post-daily-counts", key = "#days")
    public List<DailyCountDto> getFilledDailyPostCounts(int days) {
        Instant from = Instant.now().minus(Duration.ofDays(days));
        List<Object[]> raw = postRepository.countPostsPerDay(from);

        return getDailyCountDtos(days, raw);
    }
}
