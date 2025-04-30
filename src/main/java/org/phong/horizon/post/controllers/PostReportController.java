package org.phong.horizon.post.controllers;

import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.post.dtos.PostReportRequest;
import org.phong.horizon.post.dtos.PostReportResponse;
import org.phong.horizon.post.services.PostReportService;
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
    private final PostReportService reportService;

    public PostReportController(PostReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/{postId}/report")
    public ResponseEntity<RestApiResponse<PostReportResponse>> reportPost(@PathVariable UUID postId, @RequestBody PostReportRequest request) {
        return RestApiResponse.created(reportService.reportPost(postId, request));
    }

    @GetMapping("/reports")
    public ResponseEntity<RestApiResponse<List<PostReportResponse>>> getAllReports(Pageable pageable) {
        return RestApiResponse.success(reportService.getAllReports(pageable));
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<RestApiResponse<PostReportResponse>> getReportById(@PathVariable UUID id) {
        return RestApiResponse.success(reportService.getReportById(id));
    }

    @DeleteMapping("/{id}/report")
    public ResponseEntity<RestApiResponse<Void>> deleteReport(@PathVariable UUID id) {
        reportService.deleteReport(id);
        return RestApiResponse.noContent();
    }
}
