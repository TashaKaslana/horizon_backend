package org.phong.horizon.analytics.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.services.AdminNotificationAnalyticsService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics/admin-notifications")
@AllArgsConstructor
public class AdminNotificationAnalyticsController {

    private final AdminNotificationAnalyticsService adminNotificationAnalyticsService;

    /**
     * Get overview statistics for admin notifications
     */
    @GetMapping("/overview")
    public ResponseEntity<RestApiResponse<List<OverviewStatistic>>> getAdminNotificationOverview() {
        return RestApiResponse.success(adminNotificationAnalyticsService.getAdminNotificationOverview());
    }

    /**
     * Get notification trend data for the last X days
     */
    @GetMapping("/trends")
    public ResponseEntity<RestApiResponse<List<DailyCountDto>>> getNotificationTrends(
            @RequestParam(defaultValue = "30") int days) {
        return RestApiResponse.success(adminNotificationAnalyticsService.getNotificationTrendData(days));
    }

    /**
     * Get notification distribution by severity
     */
    @GetMapping("/by-severity")
    public ResponseEntity<RestApiResponse<Map<String, Long>>> getNotificationsBySeverity() {
        return RestApiResponse.success(adminNotificationAnalyticsService.getNotificationsBySeverity());
    }

    /**
     * Get notification distribution by type
     */
    @GetMapping("/by-type")
    public ResponseEntity<RestApiResponse<Map<String, Long>>> getNotificationsByType() {
        return RestApiResponse.success(adminNotificationAnalyticsService.getNotificationsByType());
    }
}
