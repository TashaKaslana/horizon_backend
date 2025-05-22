package org.phong.horizon.comment.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.report.dto.CreateReportRequest;
import org.phong.horizon.report.dto.ReportDto;
import org.phong.horizon.report.enums.ModerationItemType;
import org.phong.horizon.report.services.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentReportController {
    private final ReportService reportService;
    private final AuthService authService;

    @PostMapping("/{commentId}/report")
    public ResponseEntity<RestApiResponse<ReportDto>> reportComment(@PathVariable UUID commentId, @RequestBody org.phong.horizon.comment.dtos.CreateReportComment request) {
        UUID reporterId = authService.getUserIdFromContext();
        CreateReportRequest createReportRequest = new CreateReportRequest();
        createReportRequest.setReason(request.reason());
        createReportRequest.setItemType(ModerationItemType.COMMENT);
        createReportRequest.setCommentId(commentId);

        ReportDto createdReport = reportService.createReport(createReportRequest, reporterId);
        return RestApiResponse.created(createdReport);
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<RestApiResponse<Void>> deleteReportComment(@PathVariable("id") UUID reportId) {
        reportService.deleteReport(reportId);
        return RestApiResponse.noContent();
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<RestApiResponse<ReportDto>> getReportComment(@PathVariable("id") UUID reportId) {
        ReportDto reportDto = reportService.getReportById(reportId);
        return RestApiResponse.success(reportDto);
    }
}
