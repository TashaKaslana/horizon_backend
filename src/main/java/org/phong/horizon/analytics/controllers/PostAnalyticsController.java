package org.phong.horizon.analytics.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.services.PostAnalyticsService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/analytics/posts")
public class PostAnalyticsController {
    private PostAnalyticsService postAnalyticsService;

    @GetMapping
    public ResponseEntity<RestApiResponse<List<OverviewStatistic>>> getPostAnalytics() {
        return RestApiResponse.success(postAnalyticsService.getPostAnalytics());
    }

    @GetMapping("/daily-post-count")
    public ResponseEntity<RestApiResponse<List<OverviewStatistic.DailyPostCountDto>>> getDailyPostCount(@RequestParam int days) {
        return RestApiResponse.success(postAnalyticsService.getFilledDailyPostCounts(days));
    }
}
