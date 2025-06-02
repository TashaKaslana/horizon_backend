package org.phong.horizon.analytics.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.services.UserAnalyticsService;
import org.phong.horizon.core.responses.RestApiResponse;
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
    public ResponseEntity<RestApiResponse<List<OverviewStatistic>>> getUserAnalyticsOverview() {
        return RestApiResponse.success(userAnalyticsService.getUserAnalytics());
    }

    /**
     * Get daily user registration counts for a specified number of days.
     *
     * @param days Number of days to get data for (defaults to 30)
     * @return List of daily user counts
     */
    @GetMapping("/daily-counts")
    public ResponseEntity<RestApiResponse<List<DailyCountDto>>> getDailyUserCounts(
            @RequestParam(defaultValue = "30") int days) {
        return RestApiResponse.success(userAnalyticsService.getFilledDailyUserCounts(days));
    }
}
