package org.phong.horizon.analytics.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.services.CategoryAnalyticsService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.analytics.dtos.TopCategoryUsageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for category analytics data.
 * Provides endpoints for retrieving category statistics and analytics.
 */
@RestController
@RequestMapping("/api/admin/analytics/categories")
@AllArgsConstructor
public class CategoryAnalyticsController {

    private final CategoryAnalyticsService categoryAnalyticsService;

    /**
     * Get overview statistics for categories.
     *
     * @return List of overview statistics about categories
     */
    @GetMapping("/overview")
    public ResponseEntity<RestApiResponse<List<OverviewStatistic>>> getCategoryAnalyticsOverview() {
        return RestApiResponse.success(categoryAnalyticsService.getCategoryAnalytics());
    }

    @GetMapping("/daily-counts")
    public ResponseEntity<RestApiResponse<List<DailyCountDto>>> getDailyCategoryCounts(
            @RequestParam(defaultValue = "30") int days) {
        return RestApiResponse.success(categoryAnalyticsService.getFilledDailyCategoryCounts(days));
    }

    @GetMapping("/distribution")
    public ResponseEntity<RestApiResponse<List<TopCategoryUsageDTO>>> getCategoryDistribution(@RequestParam int days) {
        return RestApiResponse.success(categoryAnalyticsService.getCategoryDistribution(days));
    }
}
