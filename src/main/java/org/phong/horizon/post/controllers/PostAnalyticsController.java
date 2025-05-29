package org.phong.horizon.post.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.core.dtos.OverviewStatistic;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.post.dtos.DailyPostCountDto;
import org.phong.horizon.post.services.PostAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/posts/analytics")
public class PostAnalyticsController {
    private PostAnalyticsService postAnalyticsService;

    @GetMapping
    public ResponseEntity<RestApiResponse<List<OverviewStatistic>>> getPostAnalytics() {
        return RestApiResponse.success(postAnalyticsService.getPostAnalytics());
    }

    @GetMapping("/daily-post-count")
    public ResponseEntity<RestApiResponse<List<DailyPostCountDto>>> getDailyPostCount(@RequestParam int days) {
        return RestApiResponse.success(postAnalyticsService.getFilledDailyPostCounts(days));
    }
}
