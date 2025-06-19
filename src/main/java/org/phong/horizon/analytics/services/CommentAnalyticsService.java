package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.core.services.LocalizationProvider;
import org.phong.horizon.comment.infrastructure.persistence.repositories.CommentRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.phong.horizon.analytics.utils.AnalyticsHelper.calculateTrend;
import static org.phong.horizon.analytics.utils.AnalyticsHelper.getDailyCountDtos;

@Service
@AllArgsConstructor
public class CommentAnalyticsService {
    private final CommentRepository commentRepository;

    @Transactional
    @Cacheable("comment-overview")
    public List<OverviewStatistic> getCommentAnalytics() {
        long totalComments = commentRepository.count();

        // Use system default zone for logical days (or use UTC)
        ZoneId zone = ZoneOffset.UTC;

        // 1. Today's comments
        Instant todayStart = LocalDate.now(zone).atStartOfDay(zone).toInstant();
        long commentsToday = commentRepository.countByCreatedAtAfter(todayStart);

        // 2. Yesterday's comments
        Instant yesterdayStart = LocalDate.now(zone).minusDays(1).atStartOfDay(zone).toInstant();
        long commentsYesterday = commentRepository.countByCreatedAtBetween(yesterdayStart, todayStart);

        double commentTrend = calculateTrend(commentsToday, commentsYesterday);

        // 3. Reported comments
        long reportedComments = commentRepository.countReportedComments();

        // 4. Average comments per post (last 7 days)
        Instant sevenDaysAgo = LocalDate.now(zone).minusDays(7).atStartOfDay(zone).toInstant();
        Double avgCommentsPerPost = commentRepository.calculateAvgCommentsPerPost(sevenDaysAgo);
        // Handle null case and round to 1 decimal place
        double roundedAvgCommentsPerPost = avgCommentsPerPost == null ? 0.0 :
                Math.round(avgCommentsPerPost * 10.0) / 10.0;

        return List.of(
                new OverviewStatistic(
                        LocalizationProvider.getMessage("analytics.comment.total.title"),
                        String.valueOf(totalComments),
                        6.3,
                        LocalizationProvider.getMessage("analytics.comment.total.message"),
                        LocalizationProvider.getMessage("analytics.comment.total.description")
                ),
                new OverviewStatistic(
                        LocalizationProvider.getMessage("analytics.comment.today.title"),
                        String.valueOf(commentsToday),
                        commentTrend,
                        commentTrend > 0
                                ? LocalizationProvider.getMessage("analytics.comment.today.message.up")
                                : LocalizationProvider.getMessage("analytics.comment.today.message.down"),
                        LocalizationProvider.getMessage("analytics.comment.today.description")
                ),
                new OverviewStatistic(
                        LocalizationProvider.getMessage("analytics.comment.reported.title"),
                        String.valueOf(reportedComments),
                        -4.2,
                        LocalizationProvider.getMessage("analytics.comment.reported.message"),
                        LocalizationProvider.getMessage("analytics.comment.reported.description")
                ),
                new OverviewStatistic(
                        LocalizationProvider.getMessage("analytics.comment.avg_per_post.title"),
                        String.valueOf(roundedAvgCommentsPerPost),
                        1.8,
                        LocalizationProvider.getMessage("analytics.comment.avg_per_post.message"),
                        LocalizationProvider.getMessage("analytics.comment.avg_per_post.description")
                )
        );
    }

    @Transactional
    @Cacheable(value = "comment-daily-counts", key = "#days")
    public List<DailyCountDto> getFilledDailyCommentCounts(int days) {
        Instant from = Instant.now().minus(Duration.ofDays(days));
        List<Object[]> raw = commentRepository.countCommentsPerDay(from);

        return getDailyCountDtos(days, raw);
    }
}
