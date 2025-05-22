package org.phong.horizon.admin.logentry.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.phong.horizon.admin.logentry.dtos.CreateLogEntryRequest;
import org.phong.horizon.admin.logentry.dtos.LogEntryDto;
import org.phong.horizon.admin.logentry.dtos.LogSearchCriteriaDto;
import org.phong.horizon.admin.logentry.services.LogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/logs")
@AllArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    public ResponseEntity<LogEntryDto> createLogEntry(@Valid @RequestBody CreateLogEntryRequest request) {
        LogEntryDto createdLog = logService.createLogEntry(request);
        return new ResponseEntity<>(createdLog, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<LogEntryDto>> getAllLogEntries(Pageable pageable, LogSearchCriteriaDto logSearchCriteriaDto) {
        Page<LogEntryDto> logs = logService.getAllLogEntries(pageable, logSearchCriteriaDto);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogEntryDto> getLogEntryById(@PathVariable UUID id) {
        LogEntryDto log = logService.getLogEntryById(id);
        return ResponseEntity.ok(log);
    }
}

