package org.phong.horizon.analytics.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyPendingAndResolvedDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.services.ModerationReportAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics/moderation")
@AllArgsConstructor
public class ModerationReportAnalyticsController {
    private final ModerationReportAnalyticsService moderationReportAnalyticsService;

    /**
     * Gets overview statistics for moderation reports dashboard
     * @return List of overview statistics for moderation cards
     */
    @GetMapping("/overview")
    public ResponseEntity<List<OverviewStatistic>> getModerationOverview() {
        return ResponseEntity.ok(moderationReportAnalyticsService.getModerationOverviewStatistics());
    }

    /**
     * Gets daily pending and resolved reports for the last 30 days
     * @return List of daily pending and resolved reports
     */
    @GetMapping("/daily-pending-resolved")
    public ResponseEntity<List<DailyPendingAndResolvedDto>> getDailyPendingAndResolvedReports(@RequestParam int days) {
        return ResponseEntity.ok(moderationReportAnalyticsService.getDailyPendingAndResolvedCounts(days));
    }
}
