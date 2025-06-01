package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.dtos.DailyUserCountDto;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserAnalyticsService {
    private final UserRepository userRepository;

    @Transactional
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
    public List<DailyUserCountDto> getFilledDailyUserCounts(int days) {
        Instant from = Instant.now().minus(Duration.ofDays(days));
        List<Object[]> raw = userRepository.countUsersPerDay(from);

        Map<LocalDate, Long> countMap = raw.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate(),
                        row -> ((Number) row[1]).longValue()
                ));

        List<DailyUserCountDto> result = new ArrayList<>();
        LocalDate start = LocalDate.now().minusDays(days);
        LocalDate end = LocalDate.now();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            long count = countMap.getOrDefault(date, 0L);
            result.add(new DailyUserCountDto(date, count));
        }

        return result;
    }

    private double calculateTrend(long current, long previous) {
        if (previous == 0) return current == 0 ? 0.0 : 100.0;
        return ((double) (current - previous) / previous) * 100;
    }
}
