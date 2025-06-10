package org.phong.horizon.comment.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.comment.dtos.BulkCommentDeleteRequest;
import org.phong.horizon.comment.dtos.CommentRespond;
import org.phong.horizon.comment.dtos.CommentResponseWithPostDetails;
import org.phong.horizon.comment.services.CommentService;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/admin/comments")
@AllArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;


    @GetMapping()
    public ResponseEntity<RestApiResponse<List<CommentRespond>>> getAllComments(@ParameterObject Pageable pageable) {
        return RestApiResponse.success(commentService.getAllComments(pageable));
    }

    @GetMapping("with-details")
    public ResponseEntity<RestApiResponse<List<CommentResponseWithPostDetails>>> getAllCommentsWithPostDetails(@ParameterObject Pageable pageable) {
        return RestApiResponse.success(commentService.getAllCommentsWithPostDetailsByPostId(pageable));
    }

    @DeleteMapping("/all")
    @LogActivity(
            activityCode = ActivityTypeCode.COMMENT_DELETE,
            description = "Admin delete all comments",
            targetType = SystemCategory.COMMENT
    )
    public ResponseEntity<RestApiResponse<Void>> deleteAllComments() {
        commentService.deleteAllComments();
        return RestApiResponse.noContent();
    }

    @DeleteMapping("/bulk")
    @LogActivity(
            activityCode = ActivityTypeCode.COMMENT_DELETE,
            description = "Admin delete multiple comments",
            targetType = SystemCategory.COMMENT
    )
    public ResponseEntity<RestApiResponse<Void>> deleteMultipleComments(@ParameterObject BulkCommentDeleteRequest request) {
        commentService.bulkDeleteComments(request);
        return RestApiResponse.noContent();
    }
}

