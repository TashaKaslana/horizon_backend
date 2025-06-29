package org.phong.horizon.analytics.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.services.CommentAnalyticsService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for comment analytics data.
 * Provides endpoints for retrieving comment statistics and analytics.
 */
@RestController
@RequestMapping("/api/admin/analytics/comments")
@AllArgsConstructor
public class CommentAnalyticsController {

    private final CommentAnalyticsService commentAnalyticsService;

    /**
     * Get overview statistics for comments.
     *
     * @return List of overview statistics about comments
     */
    @GetMapping("/overview")
    public ResponseEntity<RestApiResponse<List<OverviewStatistic>>> getCommentAnalyticsOverview() {
        return RestApiResponse.success(commentAnalyticsService.getCommentAnalytics());
    }

    /**
     * Get daily comment counts for a specified number of days.
     *
     * @param days Number of days to get data for (defaults to 30)
     * @return List of daily comment counts
     */
    @GetMapping("/daily-counts")
    public ResponseEntity<RestApiResponse<List<DailyCountDto>>> getDailyCommentCounts(
            @RequestParam(defaultValue = "30") int days) {
        return RestApiResponse.success(commentAnalyticsService.getFilledDailyCommentCounts(days));
    }
}
