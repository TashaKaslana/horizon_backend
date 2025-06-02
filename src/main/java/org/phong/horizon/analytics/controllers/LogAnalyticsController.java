package org.phong.horizon.analytics.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.services.LogAnalyticsService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics/logs")
@AllArgsConstructor
public class LogAnalyticsController {

    private final LogAnalyticsService logAnalyticsService;

    /**
     * Get overview statistics for system logs
     */
    @GetMapping("/overview")
    public ResponseEntity<RestApiResponse<List<OverviewStatistic>>> getLogOverview() {
        return RestApiResponse.success(logAnalyticsService.getLogOverviewStatistics());
    }

    /**
     * Get daily error and critical log counts for the specified number of days
     */
    @GetMapping("/daily-errors")
    public ResponseEntity<RestApiResponse<List<DailyCountDto>>> getDailyErrorLogs(
            @RequestParam(defaultValue = "30") int days) {
        return RestApiResponse.success(logAnalyticsService.getDailyErrorAndCriticalLogCounts(days));
    }

    /**
     * Get daily breakdown of logs by severity level
     */
    @GetMapping("/daily-by-severity")
    public ResponseEntity<RestApiResponse<Map<String, List<DailyCountDto>>>> getDailyLogsBySeverity(
            @RequestParam(defaultValue = "30") int days) {
        return RestApiResponse.success(logAnalyticsService.getDailyLogsBySeverity(days));
    }
}
