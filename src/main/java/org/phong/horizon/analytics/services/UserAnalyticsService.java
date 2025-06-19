package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.core.services.LocalizationProvider;
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
                        LocalizationProvider.getMessage("analytics.user.total.title"),
                        String.valueOf(totalUsers),
                        userTrend,
                        LocalizationProvider.getMessage("analytics.user.total.message"),
                        LocalizationProvider.getMessage("analytics.user.total.description")
                ),
                new OverviewStatistic(
                        LocalizationProvider.getMessage("analytics.user.new_30d.title"),
                        String.valueOf(newUsers30d),
                        2.3,
                        LocalizationProvider.getMessage("analytics.user.new_30d.message"),
                        LocalizationProvider.getMessage("analytics.user.new_30d.description")
                ),
                new OverviewStatistic(
                        LocalizationProvider.getMessage("analytics.user.banned.title"),
                        String.valueOf(bannedUsers),
                        -5.0,
                        LocalizationProvider.getMessage("analytics.user.banned.message"),
                        LocalizationProvider.getMessage("analytics.user.banned.description")
                ),
                new OverviewStatistic(
                        LocalizationProvider.getMessage("analytics.user.verified.title"),
                        String.valueOf(verifiedUsers),
                        12.1,
                        LocalizationProvider.getMessage("analytics.user.verified.message"),
                        LocalizationProvider.getMessage("analytics.user.verified.description")
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
