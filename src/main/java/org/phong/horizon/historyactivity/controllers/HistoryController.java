package org.phong.horizon.historyactivity.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.historyactivity.dtos.ActivityDisplayDto;
import org.phong.horizon.historyactivity.dtos.HistoryActivityDto;
import org.phong.horizon.historyactivity.services.ActivityLoggingService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/histories")
public class HistoryController {
    private final ActivityLoggingService activityLoggingService;
    private final AuthService authService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<RestApiResponse<List<HistoryActivityDto>>> getHistoriesByUserId(@PathVariable UUID userId, @ParameterObject Pageable pageable) {
        return RestApiResponse.success(activityLoggingService.getAllActivitiesByUserId(userId, pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<RestApiResponse<List<ActivityDisplayDto>>> getHistoriesForMe(@ParameterObject Pageable pageable) {
        return RestApiResponse.success(activityLoggingService.getActivitiesWithJsonMessage(
                authService.getUserIdFromContext(),
                pageable
        ));
    }
}
