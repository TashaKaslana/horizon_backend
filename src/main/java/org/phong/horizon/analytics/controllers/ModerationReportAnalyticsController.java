package org.phong.horizon.analytics.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyPendingAndResolvedDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.services.ModerationReportAnalyticsService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.report.enums.ModerationItemType;
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
    public ResponseEntity<RestApiResponse<List<OverviewStatistic>>> getModerationOverview() {
        return RestApiResponse.success(moderationReportAnalyticsService.getModerationOverviewStatistics());
    }

    /**
     * Gets overview statistics specific to user moderation
     * @return List of overview statistics for user moderation
     */
    @GetMapping("/overview/user")
    public ResponseEntity<RestApiResponse<List<OverviewStatistic>>> getUserModerationOverview() {
        return RestApiResponse.success(moderationReportAnalyticsService.getModerationOverviewStatistics(ModerationItemType.USER));
    }

    /**
     * Gets overview statistics specific to post moderation
     * @return List of overview statistics for post moderation
     */
    @GetMapping("/overview/post")
    public ResponseEntity<RestApiResponse<List<OverviewStatistic>>> getPostModerationOverview() {
        return RestApiResponse.success(moderationReportAnalyticsService.getModerationOverviewStatistics(ModerationItemType.POST));
    }

    /**
     * Gets overview statistics specific to comment moderation
     * @return List of overview statistics for comment moderation
     */
    @GetMapping("/overview/comment")
    public ResponseEntity<RestApiResponse<List<OverviewStatistic>>> getCommentModerationOverview() {
        return RestApiResponse.success(moderationReportAnalyticsService.getModerationOverviewStatistics(ModerationItemType.COMMENT));
    }

    /**
     * Gets daily pending and resolved reports for the specified number of days
     * @param days Number of days to get data for
     * @param itemType Optional filter for moderation item type (USER, POST, COMMENT)
     * @return List of daily pending and resolved reports
     */
    @GetMapping("/daily-pending-resolved")
    public ResponseEntity<RestApiResponse<List<DailyPendingAndResolvedDto>>> getDailyPendingAndResolvedReports(
            @RequestParam int days,
            @RequestParam(required = false) ModerationItemType itemType) {
        return RestApiResponse.success(moderationReportAnalyticsService.getDailyPendingAndResolvedCounts(days, itemType));
    }
}
