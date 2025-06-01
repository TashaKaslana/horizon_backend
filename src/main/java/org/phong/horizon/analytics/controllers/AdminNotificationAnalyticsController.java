package org.phong.horizon.analytics.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.services.AdminNotificationAnalyticsService;
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
    public ResponseEntity<List<OverviewStatistic>> getAdminNotificationOverview() {
        return ResponseEntity.ok(adminNotificationAnalyticsService.getAdminNotificationOverview());
    }

    /**
     * Get notification trend data for the last X days
     */
    @GetMapping("/trends")
    public ResponseEntity<List<DailyCountDto>> getNotificationTrends(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(adminNotificationAnalyticsService.getNotificationTrendData(days));
    }

    /**
     * Get notification distribution by severity
     */
    @GetMapping("/by-severity")
    public ResponseEntity<Map<String, Long>> getNotificationsBySeverity() {
        return ResponseEntity.ok(adminNotificationAnalyticsService.getNotificationsBySeverity());
    }

    /**
     * Get notification distribution by type
     */
    @GetMapping("/by-type")
    public ResponseEntity<Map<String, Long>> getNotificationsByType() {
        return ResponseEntity.ok(adminNotificationAnalyticsService.getNotificationsByType());
    }
}
