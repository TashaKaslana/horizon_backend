package org.phong.horizon.comment.controllers;

import jakarta.validation.Valid;
import org.phong.horizon.comment.dtos.CommentRespond;
import org.phong.horizon.comment.dtos.CreateCommentDto;
import org.phong.horizon.comment.dtos.UpdateCommentContentDto;
import org.phong.horizon.comment.services.CommentService;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @LogActivity(
            activityCode = ActivityTypeCode.COMMENT_CREATE,
            description = "Create a new comment",
            targetType = SystemCategory.POST,
            targetIdExpression = "#result.body.data.id"
    )
    public ResponseEntity<RestApiResponse<CommentRespond>> createComment(@Valid @RequestBody CreateCommentDto request) {
        return RestApiResponse.created(commentService.createComment(request));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<RestApiResponse<CommentRespond>> getCommentById(@PathVariable UUID commentId) {
        return RestApiResponse.success(commentService.getCommentById(commentId));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<RestApiResponse<List<CommentRespond>>> getAllCommentsByPostId(@ParameterObject Pageable pageable, @PathVariable UUID postId) {
        return RestApiResponse.success(commentService.getAllCommentsByPostId(pageable, postId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<RestApiResponse<CommentRespond>> updateComment(@PathVariable UUID commentId,
                                                                         @Valid @RequestBody UpdateCommentContentDto request) {
        return RestApiResponse.success(commentService.updateCommentContent(commentId, request));
    }

    @DeleteMapping("/{commentId}")
    @LogActivity(
            activityCode = ActivityTypeCode.COMMENT_DELETE,
            description = "Delete a comment",
            targetType = SystemCategory.COMMENT,
            targetIdExpression = "#commentId"
    )
    public ResponseEntity<RestApiResponse<Void>> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteCommentById(commentId);
        return RestApiResponse.noContent();
    }

    @PatchMapping("/{commentId}/pin")
    public ResponseEntity<RestApiResponse<Void>> pinComment(@PathVariable UUID commentId) {
        commentService.pinComment(commentId);
        return RestApiResponse.noContent();
    }

    @PatchMapping("/{commentId}/unpin")
    public ResponseEntity<RestApiResponse<Void>> unpinComment(@PathVariable UUID commentId) {
        commentService.unpinComment(commentId);
        return RestApiResponse.noContent();
    }

    @PutMapping("/bulk-update")
    public ResponseEntity<RestApiResponse<List<CommentRespond>>> bulkUpdateComments(@Valid @RequestBody org.phong.horizon.comment.dtos.BulkCommentUpdateRequest request) {
        return RestApiResponse.success(commentService.bulkUpdateComments(request));
    }
}
