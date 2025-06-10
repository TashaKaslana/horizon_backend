package org.phong.horizon.report.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.report.dto.BulkReportDeleteRequest;
import org.phong.horizon.report.dto.BulkReportUpdateRequest;
import org.phong.horizon.report.dto.CreateReportRequest;
import org.phong.horizon.report.dto.ReportDto;
import org.phong.horizon.report.enums.ModerationItemType;
import org.phong.horizon.report.enums.ModerationStatus;
import org.phong.horizon.report.services.ReportService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<RestApiResponse<ReportDto>> createReport(@Valid @RequestBody CreateReportRequest request) {
        UUID reporterId = authService.getUserIdFromContext();
        ReportDto createdReport = reportService.createReport(request, reporterId);

        return RestApiResponse.created(createdReport);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<RestApiResponse<ReportDto>> getReportById(@PathVariable UUID reportId) {
        ReportDto reportDto = reportService.getReportById(reportId);
        return RestApiResponse.success(reportDto);
    }

    @GetMapping("/all")
    public ResponseEntity<RestApiResponse<List<ReportDto>>> getAllReports(@ParameterObject Pageable pageable) {
        Page<ReportDto> reports = reportService.getAllReports(pageable);
        return RestApiResponse.success(reports);
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<List<ReportDto>>> searchReports(
            @RequestParam(required = false) UUID reporterId,
            @RequestParam(required = false) UUID reportedUserId,
            @RequestParam(required = false) ModerationStatus status,
            @RequestParam(required = false) ModerationItemType itemType,
            @RequestParam(required = false) UUID itemId,
            @ParameterObject Pageable pageable) {
        Page<ReportDto> reports = reportService.searchReports(reporterId, reportedUserId, status, itemType, itemId, pageable);
        return RestApiResponse.success(reports);
    }

    @PutMapping("/{reportId}/status")
    public ResponseEntity<RestApiResponse<ReportDto>> updateReportStatus(
            @PathVariable UUID reportId,
            @RequestBody ModerationStatus newStatus) {
        ReportDto updatedReport = reportService.updateReportStatus(reportId, newStatus);
        return RestApiResponse.success(updatedReport);
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<RestApiResponse<Void>> deleteReport(@PathVariable UUID reportId) {
        reportService.deleteReport(reportId);
        return RestApiResponse.noContent();
    }

    @DeleteMapping("/bulk-delete")
    public ResponseEntity<RestApiResponse<Void>> bulkDeleteReports(@Valid @RequestBody BulkReportDeleteRequest request) {
        reportService.bulkDeleteReports(request);
        return RestApiResponse.noContent();
    }

    @PutMapping("/bulk-update")
    public ResponseEntity<RestApiResponse<List<ReportDto>>> bulkUpdateReports(@Valid @RequestBody BulkReportUpdateRequest request) {
        return RestApiResponse.success(reportService.bulkUpdateReportStatus(request));
    }
}
