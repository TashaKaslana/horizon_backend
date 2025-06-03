package org.phong.horizon.analytics.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.dtos.TopTagUsageDTO;
import org.phong.horizon.analytics.services.TagAnalyticsService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for tag analytics data.
 * Provides endpoints for retrieving tag statistics and analytics.
 */
@RestController
@RequestMapping("/api/admin/analytics/tags")
@AllArgsConstructor
public class TagAnalyticsController {

    private final TagAnalyticsService tagAnalyticsService;

    /**
     * Get overview statistics for tags.
     *
     * @return List of overview statistics about tags
     */
    @GetMapping("/overview")
    public ResponseEntity<RestApiResponse<List<OverviewStatistic>>> getTagAnalyticsOverview() {
        return RestApiResponse.success(tagAnalyticsService.getTagAnalytics());
    }

    /**
     * Get daily tag creation counts for a specified number of days.
     *
     * @param days Number of days to get data for (defaults to 30)
     * @return List of daily tag counts
     */
    @GetMapping("/daily-counts")
    public ResponseEntity<RestApiResponse<List<DailyCountDto>>> getDailyTagCounts(
            @RequestParam(defaultValue = "30") int days) {
        return RestApiResponse.success(tagAnalyticsService.getDailyTagCounts(days));
    }

    /**
     * Get tag distribution by post count
     *
     * @return Map of tag names to post counts
     */
    @GetMapping("/distribution")
    public ResponseEntity<RestApiResponse<List<TopTagUsageDTO>>> getTagDistribution(@RequestParam int days) {
        return RestApiResponse.success(tagAnalyticsService.getTagDistribution(days));
    }
}
