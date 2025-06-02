package org.phong.horizon.analytics.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.dtos.TimeSeriesDataPoint;
import org.phong.horizon.analytics.services.DashboardAnalyticsService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller for centralized dashboard analytics.
 * Provides endpoints for retrieving analytics across all domains.
 */
@RestController
@RequestMapping("/api/admin/analytics/dashboard")
@AllArgsConstructor
public class DashboardAnalyticsController {

    private final DashboardAnalyticsService analyticsService;

    /**
     * Get main dashboard overview statistics.
     *
     * @return List of dashboard statistics for the main overview cards
     */
    @GetMapping("/overview")
    public ResponseEntity<RestApiResponse<List<OverviewStatistic>>> getDashboardOverview() {
        return RestApiResponse.success(analyticsService.getDashboardOverview());
    }

    /**
     * Get daily user registration counts.
     *
     * @param days Number of days to get data for (defaults to 30)
     * @return List of time series data points for users per day
     */
    @GetMapping("/users/daily")
    public ResponseEntity<RestApiResponse<List<TimeSeriesDataPoint>>> getUsersPerDay(
            @RequestParam(defaultValue = "30") int days) {
        return RestApiResponse.success(analyticsService.getUsersPerDay(days));
    }

    /**
     * Get daily post creation counts.
     *
     * @param days Number of days to get data for (defaults to 30)
     * @return List of time series data points for posts per day
     */
    @GetMapping("/posts/daily")
    public ResponseEntity<RestApiResponse<List<TimeSeriesDataPoint>>> getPostsPerDay(
            @RequestParam(defaultValue = "30") int days) {
        return RestApiResponse.success(analyticsService.getPostsPerDay(days));
    }

    /**
     * Get daily comment counts.
     *
     * @param days Number of days to get data for (defaults to 30)
     * @return List of time series data points for comments per day
     */
    @GetMapping("/comments/daily")
    public ResponseEntity<RestApiResponse<List<TimeSeriesDataPoint>>> getCommentsPerDay(
            @RequestParam(defaultValue = "30") int days) {
        return RestApiResponse.success(analyticsService.getCommentsPerDay(days));
    }

    /**
     * Get all analytics data for the dashboard in a single call.
     *
     * @param days Number of days to include in time series data (defaults to 30)
     * @return Map containing all analytics data
     */
    @GetMapping("/all")
    public ResponseEntity<RestApiResponse<Map<String, Object>>> getAllDashboardData(
            @RequestParam(defaultValue = "30") int days) {

        // Get all necessary data
        List<OverviewStatistic> overview = analyticsService.getDashboardOverview();
        List<TimeSeriesDataPoint> usersDaily = analyticsService.getUsersPerDay(days);
        List<TimeSeriesDataPoint> postsDaily = analyticsService.getPostsPerDay(days);
        List<TimeSeriesDataPoint> commentsDaily = analyticsService.getCommentsPerDay(days);

        // Return all data in a single map
        return RestApiResponse.success(Map.of(
            "overview", overview,
            "usersDaily", usersDaily,
            "postsDaily", postsDaily,
            "commentsDaily", commentsDaily
        ));
    }
}
