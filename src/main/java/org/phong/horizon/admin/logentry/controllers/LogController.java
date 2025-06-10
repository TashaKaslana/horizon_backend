package org.phong.horizon.admin.logentry.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.phong.horizon.admin.logentry.dtos.CreateLogEntryRequest;
import org.phong.horizon.admin.logentry.dtos.LogEntryDto;
import org.phong.horizon.admin.logentry.dtos.LogSearchCriteriaDto;
import org.phong.horizon.admin.logentry.services.LogService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/logs")
@AllArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    public ResponseEntity<RestApiResponse<LogEntryDto>> createLogEntry(@Valid @RequestBody CreateLogEntryRequest request) {
        LogEntryDto createdLog = logService.createLogEntry(request);
        return RestApiResponse.created(createdLog);
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<List<LogEntryDto>>> getAllLogEntries(@ParameterObject Pageable pageable, @ParameterObject LogSearchCriteriaDto logSearchCriteriaDto) {
        Page<LogEntryDto> logs = logService.getAllLogEntries(pageable, logSearchCriteriaDto);
        return RestApiResponse.success(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<LogEntryDto>> getLogEntryById(@PathVariable UUID id) {
        LogEntryDto log = logService.getLogEntryById(id);
        return RestApiResponse.success(log);
    }

    @DeleteMapping("/bulk-delete")
    public ResponseEntity<RestApiResponse<Void>> bulkDeleteLogEntries(@Valid @RequestBody org.phong.horizon.admin.logentry.dtos.BulkLogDeleteRequest request) {
        logService.bulkDeleteLogEntries(request);
        return RestApiResponse.noContent();
    }
}
