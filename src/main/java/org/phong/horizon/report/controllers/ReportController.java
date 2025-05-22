package org.phong.horizon.report.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.report.dto.CreateReportRequest;
import org.phong.horizon.report.dto.ReportDto;
import org.phong.horizon.report.enums.ModerationItemType;
import org.phong.horizon.report.enums.ModerationStatus;
import org.phong.horizon.report.services.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<ReportDto> createReport(@Valid @RequestBody CreateReportRequest request) {
        UUID reporterId = authService.getUserIdFromContext();
        if (reporterId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        ReportDto createdReport = reportService.createReport(request, reporterId);
        return new ResponseEntity<>(createdReport, HttpStatus.CREATED);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDto> getReportById(@PathVariable UUID reportId) {
        ReportDto reportDto = reportService.getReportById(reportId);
        return new ResponseEntity<>(reportDto, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ReportDto>> getAllReports(Pageable pageable) {
        Page<ReportDto> reports = reportService.getAllReports(pageable);
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<ReportDto>> searchReports(
            @RequestParam(required = false) UUID reporterId,
            @RequestParam(required = false) UUID reportedUserId,
            @RequestParam(required = false) ModerationStatus status,
            @RequestParam(required = false) ModerationItemType itemType,
            @RequestParam(required = false) UUID itemId,
            Pageable pageable) {
        Page<ReportDto> reports = reportService.searchReports(reporterId, reportedUserId, status, itemType, itemId, pageable);
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @PutMapping("/{reportId}/status")
    public ResponseEntity<ReportDto> updateReportStatus(
            @PathVariable UUID reportId,
            @RequestBody ModerationStatus newStatus) {
        ReportDto updatedReport = reportService.updateReportStatus(reportId, newStatus);
        return new ResponseEntity<>(updatedReport, HttpStatus.OK);
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID reportId) {
        reportService.deleteReport(reportId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
