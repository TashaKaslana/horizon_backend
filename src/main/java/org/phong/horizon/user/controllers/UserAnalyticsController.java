package org.phong.horizon.user.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.core.dtos.OverviewStatistic;
import org.phong.horizon.user.dtos.DailyUserCountDto;
import org.phong.horizon.user.services.UserAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for user analytics data.
 * Provides endpoints for retrieving user statistics and analytics.
 */
@RestController
@RequestMapping("/api/admin/analytics/users")
@AllArgsConstructor
public class UserAnalyticsController {

    private final UserAnalyticsService userAnalyticsService;

    /**
     * Get overview statistics for users.
     *
     * @return List of overview statistics about users
     */
    @GetMapping("/overview")
    public ResponseEntity<List<OverviewStatistic>> getUserAnalyticsOverview() {
        return ResponseEntity.ok(userAnalyticsService.getUserAnalytics());
    }

    /**
     * Get daily user registration counts for a specified number of days.
     *
     * @param days Number of days to get data for (defaults to 30)
     * @return List of daily user counts
     */
    @GetMapping("/daily-counts")
    public ResponseEntity<List<DailyUserCountDto>> getDailyUserCounts(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(userAnalyticsService.getFilledDailyUserCounts(days));
    }
}
