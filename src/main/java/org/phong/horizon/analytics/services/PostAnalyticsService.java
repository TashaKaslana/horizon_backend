package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.core.services.LocalizationProvider;
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
                        LocalizationProvider.getMessage("analytics.post.total.title"),
                        String.valueOf(totalPosts),
                        4.1,
                        LocalizationProvider.getMessage("analytics.post.total.message"),
                        LocalizationProvider.getMessage("analytics.post.total.description")
                ),
                new OverviewStatistic(
                        LocalizationProvider.getMessage("analytics.post.today.title"),
                        String.valueOf(postsToday),
                        postTrend,
                        postTrend >= 0
                                ? LocalizationProvider.getMessage("analytics.post.today.message.up")
                                : LocalizationProvider.getMessage("analytics.post.today.message.down"),
                        LocalizationProvider.getMessage("analytics.post.today.description")
                ),
                new OverviewStatistic(
                        LocalizationProvider.getMessage("analytics.post.reported.title"),
                        String.valueOf(reportedPosts),
                        -8.4,
                        LocalizationProvider.getMessage("analytics.post.reported.message"),
                        LocalizationProvider.getMessage("analytics.post.reported.description")
                ),
                new OverviewStatistic(
                        LocalizationProvider.getMessage("analytics.post.avg_engagement.title"),
                        String.format("%.1f", avgEngagement),
                        5.9,
                        LocalizationProvider.getMessage("analytics.post.avg_engagement.message"),
                        LocalizationProvider.getMessage("analytics.post.avg_engagement.description")
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
