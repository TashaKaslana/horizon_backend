package org.phong.horizon.comment.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.comment.dtos.CreateReportComment;
import org.phong.horizon.comment.dtos.ReportCommentResponse;
import org.phong.horizon.comment.services.CommentReportService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments/reports")
@AllArgsConstructor
public class CommentReportController {
    private final CommentReportService commentReportService;

    @PostMapping
    public ResponseEntity<RestApiResponse<Void>> reportComment(@RequestBody CreateReportComment request) {
        commentReportService.reportComment(request);
        return RestApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestApiResponse<Void>> deleteReportComment(@PathVariable("id") UUID reportId) {
        commentReportService.deleteReportComment(reportId);
        return RestApiResponse.success();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<ReportCommentResponse>> getReportComment(@PathVariable("id") UUID reportId) {
        return RestApiResponse.success(commentReportService.getReportCommentById(reportId));
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<List<ReportCommentResponse>>> getAllReportComments(Pageable pageable) {
        return RestApiResponse.success(commentReportService.getAllReportComments(pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<RestApiResponse<List<ReportCommentResponse>>> getReportCommentsByUser(
            @PathVariable UUID userId,
            Pageable pageable) {
        return RestApiResponse.success(commentReportService.getReportCommentsByUserId(userId, pageable));
    }
}