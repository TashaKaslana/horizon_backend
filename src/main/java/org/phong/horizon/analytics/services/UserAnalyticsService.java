package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

import static org.phong.horizon.analytics.utils.AnalyticsHelper.calculateTrend;
import static org.phong.horizon.analytics.utils.AnalyticsHelper.getDailyCountDtos;

@Service
@AllArgsConstructor
public class UserAnalyticsService {
    private final UserRepository userRepository;

    @Transactional
    @Cacheable("user-overview")
    public List<OverviewStatistic> getUserAnalytics() {
        long totalUsers = userRepository.count();

        // Use system default zone for logical days (or use UTC)
        ZoneId zone = ZoneOffset.UTC;

        // 1. Today
        Instant todayStart = LocalDate.now(zone).atStartOfDay(zone).toInstant();
        long usersToday = userRepository.countByCreatedAtAfter(todayStart);

        // 2. Yesterday
        Instant yesterdayStart = LocalDate.now(zone).minusDays(1).atStartOfDay(zone).toInstant();
        long usersYesterday = userRepository.countByCreatedAtBetween(yesterdayStart, todayStart);

        double userTrend = calculateTrend(usersToday, usersYesterday);

        // 3. Last 30 days
        Instant thirtyDaysAgo = LocalDate.now(zone).minusDays(30).atStartOfDay(zone).toInstant();
        long newUsers30d = userRepository.countByCreatedAtAfter(thirtyDaysAgo);

        // These features aren't implemented yet, so we'll hardcode as specified
        long bannedUsers = 87;
        long verifiedUsers = 652;

        return List.of(
                new OverviewStatistic(
                        "Total Users",
                        String.valueOf(totalUsers),
                        userTrend,
                        "Up 6.8% from last month",
                        "Total registered users"
                ),
                new OverviewStatistic(
                        "New Users (30d)",
                        String.valueOf(newUsers30d),
                        2.3,
                        "Steady onboarding rate",
                        "Users signed up in the last 30 days"
                ),
                new OverviewStatistic(
                        "Banned Users",
                        String.valueOf(bannedUsers),
                        -5.0,
                        "Decrease in bans",
                        "Currently banned accounts"
                ),
                new OverviewStatistic(
                        "Verified Users",
                        String.valueOf(verifiedUsers),
                        12.1,
                        "Verification requests rising",
                        "Verified accounts to date"
                )
        );
    }

    @Transactional
    @Cacheable(value = "user-daily-counts", key = "#days")
    public List<DailyCountDto> getFilledDailyUserCounts(int days) {
        Instant from = Instant.now().minus(Duration.ofDays(days));
        List<Object[]> raw = userRepository.countUsersPerDay(from);

        return getDailyCountDtos(days, raw);
    }
}
