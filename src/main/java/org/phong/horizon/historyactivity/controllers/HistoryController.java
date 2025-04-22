package org.phong.horizon.historyactivity.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.historyactivity.dtos.HistoryActivityDto;
import org.phong.horizon.historyactivity.services.ActivityLoggingService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/histories")
public class HistoryController {
    private final ActivityLoggingService activityLoggingService;

    @GetMapping()
    public ResponseEntity<RestApiResponse<List<HistoryActivityDto>>> getHistories(UUID userId, Pageable pageable) {
        return RestApiResponse.success(activityLoggingService.getAllActivitiesByUserId(userId, pageable));
    }
}
