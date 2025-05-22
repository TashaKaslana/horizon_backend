package org.phong.horizon.post.controllers;

import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.post.dtos.PostReportRequest;
import org.phong.horizon.report.dto.CreateReportRequest;
import org.phong.horizon.report.dto.ReportDto;
import org.phong.horizon.report.enums.ModerationItemType;
import org.phong.horizon.report.services.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostReportController {
    private final ReportService reportService;
    private final AuthService authService;

    public PostReportController(ReportService reportService, AuthService authService) {
        this.reportService = reportService;
        this.authService = authService;
    }

    @PostMapping("/{postId}/report")
    public ResponseEntity<RestApiResponse<ReportDto>> reportPost(@PathVariable UUID postId, @RequestBody PostReportRequest request) {
        UUID reporterId = authService.getUserIdFromContext();
        CreateReportRequest createReportRequest = new CreateReportRequest();
        createReportRequest.setReason(request.reason());
        createReportRequest.setItemType(ModerationItemType.POST);
        createReportRequest.setPostId(postId);

        ReportDto createdReport = reportService.createReport(createReportRequest, reporterId);
        return RestApiResponse.created(createdReport);
    }

    @GetMapping("/reports")
    public ResponseEntity<RestApiResponse<List<ReportDto>>> getAllReports(Pageable pageable) {
        Page<ReportDto> reports = reportService.searchReports(null, null, null, ModerationItemType.POST, null, pageable);
        return RestApiResponse.success(reports);
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<RestApiResponse<ReportDto>> getReportById(@PathVariable UUID id) {
        ReportDto reportDto = reportService.getReportById(id);
        return RestApiResponse.success(reportDto);
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<RestApiResponse<Void>> deleteReport(@PathVariable UUID id) {
        reportService.deleteReport(id);
        return RestApiResponse.noContent();
    }
}
